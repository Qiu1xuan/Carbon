package com.qms.carbon.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * 默克尔树算法工具类
 */
public class MerkleTreeUtil {

    /**
     * 根据一组叶子节点的哈希值，计算出 Merkle Root
     * @param hashes 叶子节点的哈希列表
     * @return 最终的根哈希
     */
    public static String getMerkleRoot(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return null;
        }
        if (hashes.size() == 1) {
            return hashes.get(0);
        }

        List<String> newHashes = new ArrayList<>();
        // 两两组合计算父节点哈希
        for (int i = 0; i < hashes.size() - 1; i += 2) {
            String left = hashes.get(i);
            String right = hashes.get(i + 1);
            // 将左右子节点拼接后再次 SHA-256
            newHashes.add(HashUtil.sha256(left + right));
        }

        // 如果节点数量是奇数，最后一个节点与自己组合
        if (hashes.size() % 2 == 1) {
            String last = hashes.get(hashes.size() - 1);
            newHashes.add(HashUtil.sha256(last + last));
        }

        // 递归计算，直到只剩下一个根节点
        return getMerkleRoot(newHashes);
    }
}