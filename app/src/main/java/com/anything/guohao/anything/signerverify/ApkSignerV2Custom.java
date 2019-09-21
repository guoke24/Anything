package com.anything.guohao.anything.signerverify;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * APK Signature Scheme v2 signer.
 *
 * <p>APK Signature Scheme v2 is a whole-file signature scheme which aims to protect every single
 * bit of the APK, as opposed to the JAR Signature Scheme which protects only the names and
 * uncompressed contents of ZIP entries.
 */
/**
 * 说明，该类的代码是拷贝过来再做适配的，该类的源码来自：https://android.googlesource.com/platform/build/+/dd910c5/tools/signapk/src/com/android/signapk/ApkSignerV2.java
 * 于此同时，还从Android源码工程里，拿了两个类过来并做一些小的适配，分别是
 * frameworks/base/core/java/android/util/Pair.java
 * frameworks/base/core/java/android/util/apk/ZipUtils.java
 *
 */
public class ApkSignerV2Custom {
    /*
     * The two main goals of APK Signature Scheme v2 are:
     * 1. Detect any unauthorized modifications to the APK. This is achieved by making the signature
     *    cover every byte of the APK being signed.
     * 2. Enable much faster signature and integrity verification. This is achieved by requiring
     *    only a minimal amount of APK parsing before the signature is verified, thus completely
     *    bypassing ZIP entry decompression and by making integrity verification parallelizable by
     *    employing a hash tree.
     *
     * The generated signature block is wrapped into an APK Signing Block and inserted into the
     * original APK immediately before the start of ZIP Central Directory. This is to ensure that
     * JAR and ZIP parsers continue to work on the signed APK. The APK Signing Block is designed for
     * extensibility. For example, a future signature scheme could insert its signatures there as
     * well. The contract of the APK Signing Block is that all contents outside of the block must be
     * protected by signatures inside the block.
     */
    public static final int SIGNATURE_RSA_PSS_WITH_SHA256 = 0x0101;
    public static final int SIGNATURE_RSA_PSS_WITH_SHA512 = 0x0102;
    public static final int SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256 = 0x0103;
    public static final int SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512 = 0x0104;
    public static final int SIGNATURE_ECDSA_WITH_SHA256 = 0x0201;
    public static final int SIGNATURE_ECDSA_WITH_SHA512 = 0x0202;
    public static final int SIGNATURE_DSA_WITH_SHA256 = 0x0301;
    public static final int SIGNATURE_DSA_WITH_SHA512 = 0x0302;
    /**
     * {@code .SF} file header section attribute indicating that the APK is signed not just with
     * JAR signature scheme but also with APK Signature Scheme v2 or newer. This attribute
     * facilitates v2 signature stripping detection.
     *
     * <p>The attribute contains a comma-separated set of signature scheme IDs.
     */
    public static final String SF_ATTRIBUTE_ANDROID_APK_SIGNED_NAME = "X-Android-APK-Signed";
    // TODO: Adjust the value when signing scheme finalized.
    public static final String SF_ATTRIBUTE_ANDROID_APK_SIGNED_VALUE = "1234567890";
    private static final int CONTENT_DIGEST_CHUNKED_SHA256 = 0;
    private static final int CONTENT_DIGEST_CHUNKED_SHA512 = 1;
    private static final int CONTENT_DIGESTED_CHUNK_MAX_SIZE_BYTES = 1024 * 1024;
    private static final byte[] APK_SIGNING_BLOCK_MAGIC =
            new byte[] {
                    0x41, 0x50, 0x4b, 0x20, 0x53, 0x69, 0x67, 0x20,
                    0x42, 0x6c, 0x6f, 0x63, 0x6b, 0x20, 0x34, 0x32,
            };
    private static final int APK_SIGNATURE_SCHEME_V2_BLOCK_ID = 0x7109871a;
    private ApkSignerV2Custom() {}
    /**
     * Signer configuration.
     */
    public static final class SignerConfig {
        /** Private key. */
        public PrivateKey privateKey;
        /**
         * Certificates, with the first certificate containing the public key corresponding to
         * {@link #privateKey}.
         */
        public List<X509Certificate> certificates;
        /**
         * List of signature algorithms with which to sign (see {@code SIGNATURE_...} constants).
         */
        public List<Integer> signatureAlgorithms;
    }
    /**
     * Signs the provided APK using APK Signature Scheme v2 and returns the signed APK as a list of
     * consecutive chunks.
     *
     * <p>NOTE: To enable APK signature verifier to detect v2 signature stripping, header sections
     * of META-INF/*.SF files of APK being signed must contain the
     * {@code X-Android-APK-Signed: true} attribute.
     *
     * @param inputApk contents of the APK to be signed. The APK starts at the current position
     *        of the buffer and ends at the limit of the buffer.
     * @param signerConfigs signer configurations, one for each signer.
     *
     * @throws ApkParseException if the APK cannot be parsed.
     * @throws InvalidKeyException if a signing key is not suitable for this signature scheme or
     *         cannot be used in general.
     * @throws SignatureException if an error occurs when computing digests of generating
     *         signatures.
     */
    public static ByteBuffer[] sign(
            ByteBuffer inputApk,
            List<SignerConfig> signerConfigs)
            throws ApkParseException, InvalidKeyException, SignatureException, IOException {
        // inputApk 包含待签名apk的字节段
        // signerConfigs 签名者的配置集，包含每一个签名者

        // Slice/create a view in the inputApk to make sure that:
        // 1. inputApk is what's between position and limit of the original inputApk, and
        // 2. changes to position, limit, and byte order are not reflected in the original.
        ByteBuffer originalInputApk = inputApk;
        inputApk = originalInputApk.slice();
        inputApk.order(ByteOrder.LITTLE_ENDIAN);
        // Locate ZIP End of Central Directory (EoCD), Central Directory, and check that Central
        // Directory is immediately followed by the ZIP End of Central Directory.
        int eocdOffset = ZipUtils.findZipEndOfCentralDirectoryRecord(inputApk);
        if (eocdOffset == -1) {
            throw new ApkParseException("Failed to locate ZIP End of Central Directory");
        }
        if (ZipUtils.isZip64EndOfCentralDirectoryLocatorPresent(inputApk, eocdOffset)) {//修改点
            throw new ApkParseException("ZIP64 format not supported");
        }
        inputApk.position(eocdOffset);
        long centralDirSizeLong = ZipUtils.getZipEocdCentralDirectorySizeBytes(inputApk);
        if (centralDirSizeLong > Integer.MAX_VALUE) {
            throw new ApkParseException(
                    "ZIP Central Directory size out of range: " + centralDirSizeLong);
        }
        int centralDirSize = (int) centralDirSizeLong;
        long centralDirOffsetLong = ZipUtils.getZipEocdCentralDirectoryOffset(inputApk);
        if (centralDirOffsetLong > Integer.MAX_VALUE) {
            throw new ApkParseException(
                    "ZIP Central Directory offset in file out of range: " + centralDirOffsetLong);
        }
        int centralDirOffset = (int) centralDirOffsetLong;
        int expectedEocdOffset = centralDirOffset + centralDirSize;
        if (expectedEocdOffset < centralDirOffset) {
            throw new ApkParseException(
                    "ZIP Central Directory extent too large. Offset: " + centralDirOffset
                            + ", size: " + centralDirSize);
        }
        if (eocdOffset != expectedEocdOffset) {
            throw new ApkParseException(
                    "ZIP Central Directory not immeiately followed by ZIP End of"
                            + " Central Directory. CD end: " + expectedEocdOffset
                            + ", EoCD start: " + eocdOffset);
        }

        // Create ByteBuffers holding the contents of everything before ZIP Central Directory,
        // ZIP Central Directory, and ZIP End of Central Directory.
        inputApk.clear();
        ByteBuffer beforeCentralDir = getByteBuffer(inputApk, centralDirOffset);
        ByteBuffer centralDir = getByteBuffer(inputApk, eocdOffset - centralDirOffset);
        // Create a copy of End of Central Directory because we'll need modify its contents later.
        byte[] eocdBytes = new byte[inputApk.remaining()];
        inputApk.get(eocdBytes);
        ByteBuffer eocd = ByteBuffer.wrap(eocdBytes);
        eocd.order(inputApk.order());
        // 至此，从入参的 inputApk 中，提取出了三部分：
        // beforeCentralDir，centralDir，eocd
        // 如图所示，（需要一个图）
        // =========================================================================================

        // Figure which which digests to use for APK contents.
        Set<Integer> contentDigestAlgorithms = new HashSet<>();

        for (SignerConfig signerConfig : signerConfigs) {// 遍历每一个 *签名者配置*

            for (int signatureAlgorithm : signerConfig.signatureAlgorithms) {// 遍历当前的 *签名者配置* 内的 *算法链* 中的每一个算法
                // 把每个 *签名算法* 对应的 *摘要算法* 添加到集合
                contentDigestAlgorithms.add(
                        // 根据 *签名算法*，返回 *摘要算法*，函数内是一个简单的对应表，无复杂逻辑
                        getSignatureAlgorithmContentDigestAlgorithm(signatureAlgorithm));
            }
        }
        // 至此，从入参的 signerConfigs 中，加载了所有的摘要算法，保存到变量 contentDigestAlgorithms 中。
        // =========================================================================================

        // Compute digests of APK contents.
        Map<Integer, byte[]> contentDigests; // digest algorithm ID -> digest
        try {
            // 摘要算法ID - 分块摘要序列的摘要
            contentDigests =
                    computeContentDigests(
                            contentDigestAlgorithms,
                            new ByteBuffer[] {beforeCentralDir, centralDir, eocd});
        } catch (DigestException e) {
            throw new SignatureException("Failed to compute digests of APK", e);
        }
        // 至此，使用了 contentDigestAlgorithms 的每一个算法，分别计算出对应的 *分块摘要序列的摘要*
        // 然后以 "摘要算法ID - 分块摘要序列的摘要" 形式的键值对，保存到变量 contentDigests 中。
        // 此时的 contentDigests 变量，包含至少一个这样的键值对
        // 签名的时候，入参的 signerConfigs 带了多少中摘要算法，就会有多少个 分块摘要序列的摘要
        // =========================================================================================


        // Sign the digests and wrap the signatures and signer info into an APK Signing Block.
        ByteBuffer apkSigningBlock =
                ByteBuffer.wrap(generateApkSigningBlock(signerConfigs, contentDigests));



        // 至此 apk 签名分块制作完成
        // =========================================================================================

        // Update Central Directory Offset in End of Central Directory Record. Central Directory
        // follows the APK Signing Block and thus is shifted by the size of the APK Signing Block.
        centralDirOffset += apkSigningBlock.remaining();
        eocd.clear();
        ZipUtils.setZipEocdCentralDirectoryOffset(eocd, centralDirOffset);
        // Follow the Java NIO pattern for ByteBuffer whose contents have been consumed.
        originalInputApk.position(originalInputApk.limit());
        // Reset positions (to 0) and limits (to capacity) in the ByteBuffers below to follow the
        // Java NIO pattern for ByteBuffers which are ready for their contents to be read by caller.
        // Contrary to the name, this does not clear the contents of these ByteBuffer.
        beforeCentralDir.clear();
        centralDir.clear();
        eocd.clear();

        // Insert APK Signing Block immediately before the ZIP Central Directory.
        return new ByteBuffer[] {
                beforeCentralDir,
                apkSigningBlock,// apk 签名分块
                centralDir,
                eocd,
        };
        // 此处返回的是经过原生v2签名的apk的字节段
    }

    private static Map<Integer, byte[]> computeContentDigests(
            Set<Integer> digestAlgorithms,
            ByteBuffer[] contents) throws DigestException {

        // For each digest algorithm the result is computed as follows:
        // 1. Each segment of contents is split into consecutive chunks of 1 MB in size.
        //    The final chunk will be shorter iff the length of segment is not a multiple of 1 MB.
        //    No chunks are produced for empty (zero length) segments.
        // 2. The digest of each chunk is computed over the concatenation of byte 0xa5, the chunk's
        //    length in bytes (uint32 little-endian) and the chunk's contents.
        // 3. The output digest is computed over the concatenation of the byte 0x5a, the number of
        //    chunks (uint32 little-endian) and the concatenation of digests of chunks of all
        //    segments in-order.

        // 引述官网的中文翻译：
        // a. 第 1、3 和 4 部分的摘要采用以下计算方式，类似于两级 Merkle 树。
        // b. 每个部分都会被拆分成多个大小为 1 MB（220 个字节）的连续块。每个部分的最后一个块可能会短一些。
        // c. 每个块的摘要均通过字节 0xa5 的连接、块的长度（采用小端字节序的 uint32 值，以字节数计）和块的内容进行计算。
        // d. 顶级摘要通过字节 0x5a 的连接、块数（采用小端字节序的 uint32 值）以及块的摘要的连接（按照块在 APK 中显示的顺序）进行计算。
        // e. 摘要以分块方式计算，以便通过并行处理来加快计算速度。

        // 大致思路：
        // 因为存在多种摘要算法，这里的做法是，分别采用每一种算法：
        // 对入参 contents 的所有分块逐个计算摘要，然后逐个相连构成一个*分块摘要序列*，
        // 接着再对该*分块摘要序列*计算摘要，得到*分块摘要序列的摘要*
        // 最后，以 "算法ID-分块摘要序列的摘要" 的形式存到 map 并返回。

        // 入参解析：
        // digestAlgorithms 就是摘要算法的集合，其实就是两种摘要算法：SHA256 和 SHA512；
        // contents 就是 new ByteBuffer[] {beforeCentralDir, centralDir, eocd}。即要计算摘要的内容。

        int chunkCount = 0;
        for (ByteBuffer input : contents) { // 遍历 {beforeCentralDir, centralDir, eocd}
            chunkCount += getChunkCount(input.remaining(), CONTENT_DIGESTED_CHUNK_MAX_SIZE_BYTES/*1MB*/);// 1MB = 1024KB =1024*1024B
            // 以每个小块1MB的标准，计算一共可以拆分成多少个小块
        }
        final Map<Integer, byte[]> digestsOfChunks = new HashMap<>(digestAlgorithms.size());
        for (int digestAlgorithm : digestAlgorithms) {
            // 遍历算法集，即 SHA256 和 SHA512；

            // 当前算法下，每个1MB分块计算出的摘要的字节数
            int digestOutputSizeBytes = getContentDigestAlgorithmOutputSizeBytes(digestAlgorithm);

            // 新建一个字节数组 表示：
            // 当前算法下，所有分块的摘要的合并集，又称为：分块摘要序列
            // 同时预留了前5个字节来存储：一字节的0x5a 和 四字节的度前缀
            byte[] concatenationOfChunkCountAndChunkDigests =
                    new byte[5 + chunkCount * digestOutputSizeBytes];// 其大小为：一字节（0x5a） + 四字节（分块数，也称长度前缀） + 所有分块摘要的字节数总和
            // 下面把变量 concatenationOfChunkCountAndChunkDigests 称为 *带块数的分块摘要序列*

            // 设置 *带块数的分块摘要序列*
            // 的首字节为 0x5a
            concatenationOfChunkCountAndChunkDigests[0] = 0x5a;

            // 设置 *分块带块数的分块摘要序列* 中
            // 表示块数的4个字节为小端序，即第二到第五个字节，下标是1～4。
            setUnsignedInt32LittleEngian(
                    chunkCount, concatenationOfChunkCountAndChunkDigests, 1);

            // "算法ID - 带块数的分块摘要序列" 映射对，也称键值对
            // 存到变量 digestsOfChunks 中
            digestsOfChunks.put(digestAlgorithm, concatenationOfChunkCountAndChunkDigests);

            // 小结：每一种算法，都会对整个apk的分块序列进行一次 *摘要计算*
        }

        // ======用于存储摘要值的相关变量已经创建完毕，接下来开始计算摘要======

        int chunkIndex = 0; // 分块的下标
        byte[] chunkContentPrefix = new byte[5]; // 分块前缀，0xa5 + 四字节的块数
        chunkContentPrefix[0] = (byte) 0xa5;// 分块前缀的第一个字节
        // Optimization opportunity: digests of chunks can be computed in parallel.
        for (ByteBuffer input : contents) { // 遍历 APK 的三部分
            while (input.hasRemaining()) {
                int chunkSize =
                        Math.min(input.remaining(), CONTENT_DIGESTED_CHUNK_MAX_SIZE_BYTES/*1MB*/);
                final ByteBuffer chunk = getByteBuffer(input, chunkSize);
                // 到此可知，对 APk 的每一部分，循环的取出 1MB的小块，用于摘要计算

                for (int digestAlgorithm : digestAlgorithms) {
                    // 遍历算法集，每个算法都算一遍摘要，即 SHA256 和 SHA512；

                    String jcaAlgorithmName =
                            getContentDigestAlgorithmJcaDigestAlgorithm(digestAlgorithm);
                    MessageDigest md;
                    try {
                        md = MessageDigest.getInstance(jcaAlgorithmName);// 根据摘要算法，构建摘要计算的执行者 md
                    } catch (NoSuchAlgorithmException e) {
                        throw new DigestException(
                                jcaAlgorithmName + " MessageDigest not supported", e);
                    }
                    // Reset position to 0 and limit to capacity. Position would've been modified
                    // by the preceding iteration of this loop. NOTE: Contrary to the method name,
                    // this does not modify the contents of the chunk.
                    chunk.clear();
                    setUnsignedInt32LittleEngian(chunk.remaining(), chunkContentPrefix, 1);
                    md.update(chunkContentPrefix);// 分块前缀加入md，准备计算
                    md.update(chunk);// 分块纳入md，准备计算

                    // 获取 当前算法对应的 *带块数的分块摘要序列*，计算结果将存于此变量
                    byte[] concatenationOfChunkCountAndChunkDigests =
                            digestsOfChunks.get(digestAlgorithm);

                    // 获取 预期的*摘要字节数*
                    int expectedDigestSizeBytes =
                            getContentDigestAlgorithmOutputSizeBytes(digestAlgorithm);

                    // 获取 实际的*摘要字节数*
                    // 且将摘要结果存于变量 *带块数的分块摘要序列*，即 concatenationOfChunkCountAndChunkDigests
                    int actualDigestSizeBytes =
                            md.digest(
                                    concatenationOfChunkCountAndChunkDigests,
                                    5 + chunkIndex * expectedDigestSizeBytes,
                                    expectedDigestSizeBytes);
                            // 这里计算对象的是上面 update 进来的 chunkContentPrefix 和 chunk 的摘要
                            // 并且把摘要数据写入到 concatenationOfChunkCountAndChunkDigests 这个字节数组的指定位置
                            // 其实就是根据 chunkIndex 累加，逐个往后血、写

                    // 预期摘要字节数 必须等于 实际摘要字节数，否则抛异常
                    if (actualDigestSizeBytes != expectedDigestSizeBytes) {
                        throw new DigestException(
                                "Unexpected output size of " + md.getAlgorithm()
                                        + " digest: " + actualDigestSizeBytes);
                    }
                }
                chunkIndex++;
            }
        }

        // 计算结果（即*带块数的分块摘要序列*）
        // 存于变量 concatenationOfChunkCountAndChunkDigests，
        // 而该变量存于 digestsOfChunks
        //===========================分块摘要计算结束===================================


        Map<Integer, byte[]> result = new HashMap<>(digestAlgorithms.size());

        for (Map.Entry<Integer, byte[]> entry : digestsOfChunks.entrySet()) {
            int digestAlgorithm = entry.getKey();
            byte[] concatenationOfChunkCountAndChunkDigests = entry.getValue();
            String jcaAlgorithmName = getContentDigestAlgorithmJcaDigestAlgorithm(digestAlgorithm);
            MessageDigest md;
            try {
                md = MessageDigest.getInstance(jcaAlgorithmName);
            } catch (NoSuchAlgorithmException e) {
                throw new DigestException(jcaAlgorithmName + " MessageDigest not supported", e);
            }
            // 此处对 *带块数的分块摘要序列* 这整个变量，又进行来一次摘要计算
            // 得到 摘要算法ID - 带块数的分块摘要序列的摘要，
            // 简称 算法ID - 分块摘要序列的摘要
            // 存于 result
            result.put(digestAlgorithm, md.digest(concatenationOfChunkCountAndChunkDigests));
        }

        return result;
    }

    private static final int getChunkCount(int inputSize, int chunkSize) {
        return (inputSize + chunkSize - 1) / chunkSize;
    }

    private static void setUnsignedInt32LittleEngian(int value, byte[] result, int offset) {
        result[offset] = (byte) (value & 0xff);
        result[offset + 1] = (byte) ((value >> 8) & 0xff);
        result[offset + 2] = (byte) ((value >> 16) & 0xff);
        result[offset + 3] = (byte) ((value >> 24) & 0xff);
    }

    private static byte[] generateApkSigningBlock(
            List<SignerConfig> signerConfigs,
            Map<Integer, byte[]> contentDigests) throws InvalidKeyException, SignatureException {

        // 此处入参的是 contentDigests，即 map< 算法ID - 分块摘要序列的摘要 >
        byte[] apkSignatureSchemeV2Block =
                generateApkSignatureSchemeV2Block(signerConfigs, contentDigests);
                // 该函数 会产生 是原生V2签名分块value值

        // 此处的入参 apkSignatureSchemeV2Block 是原生V2签名分块value值
        return generateApkSigningBlock(apkSignatureSchemeV2Block);
        // 此处返回的已经是完整的 apk 签名分块
        // 即加上前后size，prelen，ID，magic，
    }

    private static byte[] generateApkSigningBlock(byte[] apkSignatureSchemeV2Block) {
        // FORMAT:
        // uint64:  size (excluding this field)
        // repeated ID-value pairs:
        //     uint64:           size (excluding this field)
        //     uint32:           ID
        //     (size - 4) bytes: value
        // uint64:  size (same as the one above)
        // uint128: magic
        int resultSize =
                8 // size
                        + 8 + 4 + apkSignatureSchemeV2Block.length // v2Block as ID-value pair
                        + 8 // size
                        + 16 // magic
                ;
        ByteBuffer result = ByteBuffer.allocate(resultSize);
        result.order(ByteOrder.LITTLE_ENDIAN);
        long blockSizeFieldValue = resultSize - 8;
        result.putLong(blockSizeFieldValue);
        long pairSizeFieldValue = 4 + apkSignatureSchemeV2Block.length;
        result.putLong(pairSizeFieldValue);
        result.putInt(APK_SIGNATURE_SCHEME_V2_BLOCK_ID);
        result.put(apkSignatureSchemeV2Block);
        result.putLong(blockSizeFieldValue);
        result.put(APK_SIGNING_BLOCK_MAGIC);
        return result.array();
    }

    // 返回值就是 原生V2签名分块value值
    private static byte[] generateApkSignatureSchemeV2Block(
            List<SignerConfig> signerConfigs,
            Map<Integer, byte[]> contentDigests) throws InvalidKeyException, SignatureException {
        // 入参的 signerConfigs 是sign函数的原始入参，表示签名者配置集
        // 入参的 contentDigests 就是 map< 摘要算法ID - 分块摘要序列的摘要 >

        // FORMAT:
        // * length-prefixed sequence of length-prefixed signer blocks.
        List<byte[]> signerBlocks = new ArrayList<>(signerConfigs.size());
        int signerNumber = 0;
        for (SignerConfig signerConfig : signerConfigs) {// 遍历 签名配置链
            signerNumber++;
            byte[] signerBlock;
            try {
                // 遍历每个 signerConfig，再和同一个 contentDigests，构造 signer
                signerBlock = generateSignerBlock(signerConfig, contentDigests);
                // 摘要链，含有多个"签名算法-摘要"对
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Signer #" + signerNumber + " failed", e);
            } catch (SignatureException e) {
                throw new SignatureException("Signer #" + signerNumber + " failed", e);
            }
            // signer 的集合
            signerBlocks.add(signerBlock);
        }

        // 其实就是 追加长度前缀
        return encodeAsSequenceOfLengthPrefixedElements(
                new byte[][] {
                        encodeAsSequenceOfLengthPrefixedElements(signerBlocks),// 对每个 signerBlock 追加长度前缀
                });

        // 返回值就是 原生V2签名分块value值
    }


    // 根据入参的 signerConfig 和 contentDigests，构造 signer
    private static byte[] generateSignerBlock(
            SignerConfig signerConfig,
            Map<Integer, byte[]> contentDigests) throws InvalidKeyException, SignatureException {
        // 入参的 signerConfig，即签名者配置，内部带有*证书序列*和*签名算法序列*
        // 入参的 contentDigests 就是 map< 摘要算法ID - 分块摘要序列的摘要 >

        if (signerConfig.certificates.isEmpty()) {
            throw new SignatureException("No certificates configured for signer");
        }

        // 根据入参 signerConfig，获得 公钥
        PublicKey publicKey = signerConfig.certificates.get(0).getPublicKey();
        byte[] encodedPublicKey = encodePublicKey(publicKey);

        // 新建 signedData 实例
        V2SignatureSchemeBlock.SignedData signedData = new V2SignatureSchemeBlock.SignedData();

        // 根据入参 signerConfig，获得 证书序列，并赋值给 signedData.certificates
        try {
            signedData.certificates = encodeCertificates(signerConfig.certificates);
        } catch (CertificateEncodingException e) {
            throw new SignatureException("Failed to encode certificates", e);
        }

        List<Pair<Integer, byte[]>> digests =
                new ArrayList<>(signerConfig.signatureAlgorithms.size());

        // 遍历入参 signerConfig 的算法序列：signatureAlgorithms
        for (int signatureAlgorithm : signerConfig.signatureAlgorithms) {

            // 取到 摘要算法ID
            int contentDigestAlgorithm =
                    getSignatureAlgorithmContentDigestAlgorithm(signatureAlgorithm);
                    // 根据 签名算法的类型，返回 摘要算法，内有一个简单对应表

            // 取到 apk的分块摘要序列的摘要
            byte[] contentDigest = contentDigests.get(contentDigestAlgorithm);

            if (contentDigest == null) {
                throw new RuntimeException(
                        getContentDigestAlgorithmJcaDigestAlgorithm(contentDigestAlgorithm)
                                + " content digest for "
                                + getSignatureAlgorithmJcaSignatureAlgorithm(signatureAlgorithm)
                                + " not computed");
            }

            // 构造 "签名算法ID-摘要序列"，保存到变量 digests
            digests.add(Pair.create(signatureAlgorithm, contentDigest));
        }

        signedData.digests = digests;// 多个"签名算法ID-摘要"对

        V2SignatureSchemeBlock.Signer signer = new V2SignatureSchemeBlock.Signer();


        // ====== 开始构造待签名数据：signer.signedData ======

        // FORMAT:
        // * length-prefixed sequence of length-prefixed digests:
        //   * uint32: signature algorithm ID
        //   * length-prefixed bytes: digest of contents
        // * length-prefixed sequence of certificates:
        //   * length-prefixed bytes: X.509 certificate (ASN.1 DER encoded).
        // * length-prefixed sequence of length-prefixed additional attributes:
        //   * uint32: ID
        //   * (length - 4) bytes: value
        signer.signedData = encodeAsSequenceOfLengthPrefixedElements(new byte[][] {
                encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(signedData.digests),//x-1
                encodeAsSequenceOfLengthPrefixedElements(signedData.certificates),//x-2
                new byte[0],//x-3
        });

        // x-1处，入参是 digests["签名算法ID-摘要"]
        // 返回值是 digests["len-(签名算法ID-(len-摘要))"]

        // x-2处，入参是 certificates["certificate"],
        // 返回值是 certificates["len-certificate"]

        // x-3处，是附加数据，原生 V2 签名不带附加数据。

        // 最终，signer.signedData 格式是：
        // signedData
        // {
        //     len-digests["len-(签名算法ID-(len-摘要))"],
        //     len-certificates["len-certificate"],
        //     len-additional_attributes["len-(ID-(len-attribute))"],
        // }

        // ====== 完成构造待签名数据：signer.signedData ======

        // ====== 开始构造签名值：signer.signatures ======
        signer.publicKey = encodedPublicKey;
        signer.signatures = new ArrayList<>();
        for (int signatureAlgorithm : signerConfig.signatureAlgorithms) {
            Pair<String, ? extends AlgorithmParameterSpec> signatureParams =
                    getSignatureAlgorithmJcaSignatureAlgorithm(signatureAlgorithm);
            String jcaSignatureAlgorithm = signatureParams.getFirst();
            AlgorithmParameterSpec jcaSignatureAlgorithmParams = signatureParams.getSecond();
            byte[] signatureBytes;
            try {
                Signature signature = Signature.getInstance(jcaSignatureAlgorithm);
                signature.initSign(signerConfig.privateKey);// 使用私钥签名
                if (jcaSignatureAlgorithmParams != null) {
                    signature.setParameter(jcaSignatureAlgorithmParams);
                }
                signature.update(signer.signedData);// 对构造好的整个 signer.signedData ，使用上述引入的私钥，进行签名
                signatureBytes = signature.sign();  // 签名后的数据
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Failed sign using " + jcaSignatureAlgorithm, e);
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                    | SignatureException e) {
                throw new SignatureException("Failed sign using " + jcaSignatureAlgorithm, e);
            }

            // 测试*公钥*能否解*私钥签名后的数据*
            try {
                Signature signature = Signature.getInstance(jcaSignatureAlgorithm);
                signature.initVerify(publicKey);
                if (jcaSignatureAlgorithmParams != null) {
                    signature.setParameter(jcaSignatureAlgorithmParams);
                }
                signature.update(signer.signedData);
                if (!signature.verify(signatureBytes)) {
                    throw new SignatureException("Signature did not verify");
                }
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Failed to verify generated " + jcaSignatureAlgorithm
                        + " signature using public key from certificate", e);
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                    | SignatureException e) {
                throw new SignatureException("Failed to verify generated " + jcaSignatureAlgorithm
                        + " signature using public key from certificate", e);
            }//end 测试*公钥*能否解*私钥签名后的数据*

            // 到此，通过验验证，*公钥*可以解*私钥*
            // 构造pair对，结构为"签名算法ID-签名后数据"
            signer.signatures.add(Pair.create(signatureAlgorithm, signatureBytes));
        }
        // ====== 完成构造签名值：signer.signatures ======

        // FORMAT:
        // * length-prefixed signed data
        // * length-prefixed sequence of length-prefixed signatures:
        //   * uint32: signature algorithm ID
        //   * length-prefixed bytes: signature of signed data
        // * length-prefixed bytes: public key (X.509 SubjectPublicKeyInfo, ASN.1 DER encoded)
        return encodeAsSequenceOfLengthPrefixedElements(
                new byte[][] {
                        signer.signedData,
                        encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(
                                signer.signatures),//y-1
                        signer.publicKey,
                });
        // y-1处，入参的是 signer.signatures["签名算法ID-签名值"]，
        // 返回值是 signer.signatures["len-(签名算法ID-(len-signature))"]

        // 最终，return 返回值是：
        // 		signer
        //		{
        //			len-signedData, //待签名数据
        //			{
        //				len-digests["len-(签名算法ID-(len-摘要))"],
        //				len-certificates["len-certificate"],
        //				len-additional_attributes["len-(ID-(len-attribute))"],
        //			}
        //			len-signatures["len-(签名算法ID-(len-signature))"],// 签名值
        //			len-publicKey // 公钥，第一个 certificate 内取到的
        //		}
    }

    private static final class V2SignatureSchemeBlock {
        private static final class Signer {
            public byte[] signedData;
            public List<Pair<Integer, byte[]>> signatures;
            public byte[] publicKey;
        }
        private static final class SignedData {
            public List<Pair<Integer, byte[]>> digests;
            public List<byte[]> certificates;
        }
    }

    private static byte[] encodePublicKey(PublicKey publicKey) throws InvalidKeyException {
        byte[] encodedPublicKey = null;
        if ("X.509".equals(publicKey.getFormat())) {
            encodedPublicKey = publicKey.getEncoded();
        }
        if (encodedPublicKey == null) {
            try {
                encodedPublicKey =
                        KeyFactory.getInstance(publicKey.getAlgorithm())
                                .getKeySpec(publicKey, X509EncodedKeySpec.class)
                                .getEncoded();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new InvalidKeyException(
                        "Failed to obtain X.509 encoded form of public key " + publicKey
                                + " of class " + publicKey.getClass().getName(),
                        e);
            }
        }
        if ((encodedPublicKey == null) || (encodedPublicKey.length == 0)) {
            throw new InvalidKeyException(
                    "Failed to obtain X.509 encoded form of public key " + publicKey
                            + " of class " + publicKey.getClass().getName());
        }
        return encodedPublicKey;
    }

    public static List<byte[]> encodeCertificates(List<X509Certificate> certificates)
            throws CertificateEncodingException {
        List<byte[]> result = new ArrayList<>();
        for (X509Certificate certificate : certificates) {
            result.add(certificate.getEncoded());
        }
        return result;
    }

    // 把入参的 List<byte[]> 类型 转为 byte[][]，再调用同名函数
    private static byte[] encodeAsSequenceOfLengthPrefixedElements(List<byte[]> sequence) {
        return encodeAsSequenceOfLengthPrefixedElements(
                sequence.toArray(new byte[sequence.size()][]));
    }

    // 入参格式 sequence of element
    // 返回格式 sequence of "len - element"
    private static byte[] encodeAsSequenceOfLengthPrefixedElements(byte[][] sequence) {
        int payloadSize = 0;
        for (byte[] element : sequence) {
            payloadSize += 4 + element.length;
        }
        ByteBuffer result = ByteBuffer.allocate(payloadSize);
        result.order(ByteOrder.LITTLE_ENDIAN);
        for (byte[] element : sequence) {
            result.putInt(element.length);
            result.put(element);
        }
        return result.array();
    }

    // 入参格式 sequence of "ID - value"
    // 返回格式 sequence of "len -（ID - （len - value））"
    private static byte[] encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(
            List<Pair<Integer, byte[]>> sequence) {
        int resultSize = 0;
        for (Pair<Integer, byte[]> element : sequence) {
            resultSize += 12 + element.getSecond().length;
        }
        ByteBuffer result = ByteBuffer.allocate(resultSize);
        result.order(ByteOrder.LITTLE_ENDIAN);

                                                // 以 signedData.digests 为例：
        for (Pair<Integer, byte[]> element : sequence) {
                                                // 遍历 signedData.digests 中的每个元素
            byte[] second = element.getSecond();// 获取 digest 的字节段
                                                // 每个 digest，都用以下格式构造

                                                // 格式：
            result.putInt(8 + second.length);   // 4字节的长度前缀 + 4字节的算法ID
            result.putInt(element.getFirst());  // 算法ID
            result.putInt(second.length);       // 4字节的长度前缀
            result.put(second);                 // digest 的字节段
        }
        return result.array();
    }

    /**
     * Relative <em>get</em> method for reading {@code size} number of bytes from the current
     * position of this buffer.
     *
     * <p>This method reads the next {@code size} bytes at this buffer's current position,
     * returning them as a {@code ByteBuffer} with start set to 0, limit and capacity set to
     * {@code size}, byte order set to this buffer's byte order; and then increments the position by
     * {@code size}.
     */
    private static ByteBuffer getByteBuffer(ByteBuffer source, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size: " + size);
        }
        int originalLimit = source.limit();
        int position = source.position();
        int limit = position + size;
        if ((limit < position) || (limit > originalLimit)) {
            throw new BufferUnderflowException();
        }
        source.limit(limit);
        try {
            ByteBuffer result = source.slice();
            result.order(source.order());
            source.position(limit);
            return result;
        } finally {
            source.limit(originalLimit);
        }
    }

    private static Pair<String, ? extends AlgorithmParameterSpec>

    getSignatureAlgorithmJcaSignatureAlgorithm(int sigAlgorithm) {
        switch (sigAlgorithm) {
            case SIGNATURE_RSA_PSS_WITH_SHA256:
                return Pair.create(
                        "SHA256withRSA/PSS",
                        new PSSParameterSpec(
                                "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 256 / 8, 1));
            case SIGNATURE_RSA_PSS_WITH_SHA512:
                return Pair.create(
                        "SHA512withRSA/PSS",
                        new PSSParameterSpec(
                                "SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 512 / 8, 1));
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256:
                return Pair.create("SHA256withRSA", null);
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512:
                return Pair.create("SHA512withRSA", null);
            case SIGNATURE_ECDSA_WITH_SHA256:
                return Pair.create("SHA256withECDSA", null);
            case SIGNATURE_ECDSA_WITH_SHA512:
                return Pair.create("SHA512withECDSA", null);
            case SIGNATURE_DSA_WITH_SHA256:
                return Pair.create("SHA256withDSA", null);
            case SIGNATURE_DSA_WITH_SHA512:
                return Pair.create("SHA512withDSA", null);
            default:
                throw new IllegalArgumentException(
                        "Unknown signature algorithm: 0x"
                                + Long.toHexString(sigAlgorithm & 0xffffffff));
        }
    }

    private static int getSignatureAlgorithmContentDigestAlgorithm(int sigAlgorithm) {
        switch (sigAlgorithm) {
            case SIGNATURE_RSA_PSS_WITH_SHA256:
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256:
            case SIGNATURE_ECDSA_WITH_SHA256:
            case SIGNATURE_DSA_WITH_SHA256:
                return CONTENT_DIGEST_CHUNKED_SHA256;
            case SIGNATURE_RSA_PSS_WITH_SHA512:
            case SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512:
            case SIGNATURE_ECDSA_WITH_SHA512:
            case SIGNATURE_DSA_WITH_SHA512:
                return CONTENT_DIGEST_CHUNKED_SHA512;
            default:
                throw new IllegalArgumentException(
                        "Unknown signature algorithm: 0x"
                                + Long.toHexString(sigAlgorithm & 0xffffffff));
        }
    }

    private static String getContentDigestAlgorithmJcaDigestAlgorithm(int digestAlgorithm) {
        switch (digestAlgorithm) {
            case CONTENT_DIGEST_CHUNKED_SHA256:
                return "SHA-256";
            case CONTENT_DIGEST_CHUNKED_SHA512:
                return "SHA-512";
            default:
                throw new IllegalArgumentException(
                        "Unknown content digest algorthm: " + digestAlgorithm);
        }
    }

    private static int getContentDigestAlgorithmOutputSizeBytes(int digestAlgorithm) {
        switch (digestAlgorithm) {
            case CONTENT_DIGEST_CHUNKED_SHA256:
                return 256 / 8;
            case CONTENT_DIGEST_CHUNKED_SHA512:
                return 512 / 8;
            default:
                throw new IllegalArgumentException(
                        "Unknown content digest algorthm: " + digestAlgorithm);
        }
    }

    /**
     * Indicates that APK file could not be parsed.
     */
    public static class ApkParseException extends Exception {
        private static final long serialVersionUID = 1L;
        public ApkParseException(String message) {
            super(message);
        }
        public ApkParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

