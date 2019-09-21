apk
{
    beforeCentralDir,
    apkSigningBlock,// apk 签名分块
    centralDir,
    eocd,
}

函数：generateApkSigningBlock(byte[] apkSignatureSchemeV2Block) 构建：
apkSigningBlock
{
    blockSizeFieldValue,
    pairSizeFieldValue,
    APK_SIGNATURE_SCHEME_V2_BLOCK_ID,
    apkSignatureSchemeV2Block,// V2签名分块value值
    blockSizeFieldValue,
    APK_SIGNING_BLOCK_MAGIC,
}

函数：generateApkSignatureSchemeV2Block 构建：
apkSignatureSchemeV2Block
{
    len-signerBlocks[len-signerBlock]
}

        其中，
        signerBlocks[len-signerBlock] = encodeAsSequenceOfLengthPrefixedElements(signerBlocks)
        len-signerBlocks[len-signerBlock] = encodeAsSequenceOfLengthPrefixedElements(
                                                                new byte[][] {
                                                                        encodeAsSequenceOfLengthPrefixedElements(signerBlocks),
                                                                })

        apkSignatureSchemeV2Block
        {
            encodeAsSequenceOfLengthPrefixedElements(
                            new byte[][] {
                                    encodeAsSequenceOfLengthPrefixedElements(signerBlocks),
                            })
        }


        signerBlocks是一个集合
        signerBlocks.add(signerBlock)

        函数：generateSignerBlock 构建：
        signerBlock
        {
            encodeAsSequenceOfLengthPrefixedElements(
                            new byte[][] {
                                    signer.signedData,
                                    encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(
                                            signer.signatures),
                                    signer.publicKey,
                            })
        }


        令 signer.signatures["len-(ID-(len-value))"] = encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(
                                            signer.signatures),

        signerBlock
        {
            encodeAsSequenceOfLengthPrefixedElements(
                            new byte[][] {
                                    signer.signedData,
                                    signer.signatures["len-(ID-(len-value))"],
                                    signer.publicKey,
                            })
        }


最终得：
signerBlock
{
    len-signer.signedData,
    len-signer.signatures["len-(签名算法ID-(len-signature))"],
    len-signer.publicKey,
}



        在源码中找到：
        ```
        signer.signedData = encodeAsSequenceOfLengthPrefixedElements(new byte[][] {
                        encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(signedData.digests),
                        encodeAsSequenceOfLengthPrefixedElements(signedData.certificates),
                        new byte[0],
                });

        ......
        digests.add(Pair.create(signatureAlgorithm, contentDigest));// "签名算法-摘要"对
        ......
        signedData.digests = digests;// 多个"签名算法-摘要"对
        ```

        signedData.digests["len-(签名算法ID-(len-摘要))"] = encodeAsSequenceOfLengthPrefixedPairsOfIntAndLengthPrefixedBytes(signedData.digests),
        signedData.certificates["len-certificate"] = encodeAsSequenceOfLengthPrefixedElements(signedData.certificates)

        则有：
        signer.signedData
        {
            encodeAsSequenceOfLengthPrefixedElements(new byte[][] {
                            signedData.digests["len-(签名算法ID-(len-摘要))"],
                            signedData.certificates["len-certificate"],
                            new byte[0],
            }
        }

最终得：
signer.signedData
{
    len-signedData.digests["len-(签名算法ID-(len-摘要))"],
    len-signedData.certificates["len-certificate"],
    len-new byte[0],
}