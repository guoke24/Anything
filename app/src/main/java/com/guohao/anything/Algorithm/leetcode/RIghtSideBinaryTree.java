package com.guohao.anything.Algorithm.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


// 给定一棵二叉树，想象自己站在它的右侧，按照从顶部到底部的顺序，返回从右侧所能看到的节点值。
// https://leetcode-cn.com/problems/binary-tree-right-side-view/
// 解法均来源于：https://leetcode-cn.com/problems/binary-tree-right-side-view/solution/er-cha-shu-de-you-shi-tu-by-leetcode-solution/
public class RIghtSideBinaryTree {

    // 深度优先的遍历
    // 借助 stack 的后进先出的特性
    class Solution {
        public List<Integer> rightSideView(TreeNode root) {
            Map<Integer, Integer> rightmostValueAtDepth = new HashMap<Integer, Integer>();
            int max_depth = -1;

            Stack<TreeNode> nodeStack = new Stack<>();
            Stack<Integer> depthStack = new Stack<>();
            nodeStack.push(root);
            depthStack.push(0);

            while (!nodeStack.isEmpty()) {
                TreeNode node = nodeStack.pop();
                int depth = depthStack.pop();

                if (node != null) {
                    // 维护二叉树的最大深度
                    max_depth = Math.max(max_depth, depth);

                    // 如果不存在对应深度的节点我们才插入
                    if (!rightmostValueAtDepth.containsKey(depth)) {
                        rightmostValueAtDepth.put(depth, node.val);
                    }

                    nodeStack.push(node.left);  // 入栈时，先左后右
                    nodeStack.push(node.right); // 出栈时，先右后左，再配合上面的逻辑：不存在对应深度的节点我们才插入，可做到优先放右边的节点，没有再放左边的街边
                    depthStack.push(depth + 1);
                    depthStack.push(depth + 1);
                }
            }

            List<Integer> rightView = new ArrayList<Integer>();
            for (int depth = 0; depth <= max_depth; depth++) {
                rightView.add(rightmostValueAtDepth.get(depth));
            }

            return rightView;
        }
    }

    // 另一种深度优先的遍历
    // 不借助 stack，代码更简洁
    // depth 表示当前节点所处的深度
    class Solution1 {
        List<Integer> res = new ArrayList<>();

        public List<Integer> rightSideView(TreeNode root) {
            dfs(root, 0);
            return res;
        }
        private void dfs(TreeNode root, int depth) {
            if (root == null) {
                return;
            }

            // 如果 res 的长度 == 当前深度，(因为深度从 0 开始)
            // 说明当前深度的最右边节点没有放入 res，
            // 而当前算法保证了：每层最先被访问的就是最右边的节点，
            // 所以，当前节点就是当前深度的最右边节点！
            if (res.size() == depth) {
                res.add(root.val);
            }

            depth++;
            dfs(root.right, depth);
            dfs(root.left, depth);
        }
    }

    // 上述两种深度优先的遍历的解法，时间复杂度为 n，空间复杂度也为：n （一颗二叉树，最小的遍历空间为 logn，即只有一边的情况）
    // 仅仅是代码风格的区别

    // 广度优先的遍历
    // 用到了两个队列
    class Solution2 {
        public List<Integer> rightSideView(TreeNode root) {
            Map<Integer, Integer> rightmostValueAtDepth = new HashMap<Integer, Integer>();
            int max_depth = -1;

            Queue<TreeNode> nodeQueue = new LinkedList<TreeNode>();
            Queue<Integer> depthQueue = new LinkedList<Integer>();
            nodeQueue.add(root);
            depthQueue.add(0);

            while (!nodeQueue.isEmpty()) {
                TreeNode node = nodeQueue.remove();
                int depth = depthQueue.remove();

                if (node != null) {
                    // 维护二叉树的最大深度
                    max_depth = Math.max(max_depth, depth);

                    // 由于每一层最后一个访问到的节点才是我们要的答案，因此不断更新对应深度的信息即可
                    rightmostValueAtDepth.put(depth, node.val);

                    nodeQueue.add(node.left);
                    nodeQueue.add(node.right);
                    depthQueue.add(depth + 1);
                    depthQueue.add(depth + 1);
                }
            }

            List<Integer> rightView = new ArrayList<Integer>();
            for (int depth = 0; depth <= max_depth; depth++) {
                rightView.add(rightmostValueAtDepth.get(depth));
            }

            return rightView;
        }
    }

    // 广度优先的遍历的另一种写法
    // 依赖更少的变量，仅用一个队列
    class Solution3 {

        public List<Integer> rightSideView(TreeNode root) {
            List<Integer> res = new ArrayList<>();
            if(root == null) return res;

            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);

            while(!queue.isEmpty()){
                int size = queue.size();
                for (int i = 0; i < size; i++){ // 每次遍历当前层的节点

                    // 把下一层的节点入队 begin
                    TreeNode treeNode = queue.poll();
                    if(treeNode.left != null){
                        queue.offer(treeNode.left);
                    }
                    if(treeNode.right != null){
                        queue.offer(treeNode.right);
                    }// 把下一层的节点入队 end
                    // 虽然把下一层节点入队，但是并不会遍历到他们

                    if(i == size - 1){ // 遍历到当前层到最后一个节点时，加入结果队列
                        res.add(treeNode.val);
                    }
                }
            }
            return res;
        }
    }

    // 上述两种广度优先的遍历的解法，时间复杂度为 n，空间复杂度也为：n
    // 其实第一种的空间复杂度为 2n，因为用到了两个队列，并且存储操作也会多一些

    // 启示：借助队列，可以对树实现层级的遍历！

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
}
// 栈，队列的入队出队函数的区别：
// add offor peek poll remove 等