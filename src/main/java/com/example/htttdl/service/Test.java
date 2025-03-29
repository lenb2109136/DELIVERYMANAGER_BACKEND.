package com.example.htttdl.service;

import java.util.*;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import com.example.htttdl.modal.DonHang;

public class Test {
    public static void main(String[] args) {
        List<DonHang> orders = generateDonHang();
        int numShippers = 5;

        // Phân cụm ban đầu bằng DBSCAN
        Map<String, List<DonHang>> clusteredOrders = clusterDonHang(orders, 0.2, 0);

        // Tự động điều chỉnh số cụm để đảm bảo số đơn chia đều cho shipper
        Map<String, List<DonHang>> balancedClusters = balanceClusters(clusteredOrders, numShippers);

        // In kết quả
        printClusters(balancedClusters);
    }

    /**
     * Phân cụm đơn hàng bằng DBSCAN
     */
    public static Map<String, List<DonHang>> clusterDonHang(List<DonHang> donHangList, double eps, int minPts) {
        List<DoublePoint> points = new ArrayList<>();
        Map<DoublePoint, List<DonHang>> pointToDonHangs = new HashMap<>();

        // Chuyển đổi danh sách đơn hàng thành danh sách điểm tọa độ
        for (DonHang donHang : donHangList) {
            double[] coordinates = { donHang.getKinhDo(), donHang.getViDo() };
            DoublePoint point = new DoublePoint(coordinates);
            pointToDonHangs.computeIfAbsent(point, k -> new ArrayList<>()).add(donHang);
            points.add(point);
        }

        // Thực hiện DBSCAN clustering
        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<>(eps, minPts);
        List<Cluster<DoublePoint>> clusters = dbscan.cluster(points);

        // Chuyển kết quả sang Map<String, List<DonHang>>
        Map<String, List<DonHang>> clusteredOrders = new HashMap<>();
        int clusterIndex = 1;

        for (Cluster<DoublePoint> cluster : clusters) {
            String clusterKey = "cum" + clusterIndex++;
            List<DonHang> donHangs = new ArrayList<>();

            for (DoublePoint point : cluster.getPoints()) {
                donHangs.addAll(pointToDonHangs.get(point));
            }

            clusteredOrders.put(clusterKey, donHangs);
        }

        return clusteredOrders;
    }

    /**
     * Cân bằng số cụm để phân phối đơn hàng đều hơn cho shipper
     */
    public static Map<String, List<DonHang>> balanceClusters(Map<String, List<DonHang>> clusters, int numShippers) {
        List<List<DonHang>> clusterList = new ArrayList<>(clusters.values());
        int totalOrders = clusterList.stream().mapToInt(List::size).sum();
        int avgOrdersPerShipper = totalOrders / numShippers;
        int numClusters = clusterList.size();

        // Nếu số cụm ban đầu ít hơn số shipper, cần chia tách thêm
        while (numClusters < numShippers) {
            // Tìm cụm lớn nhất
            clusterList.sort((a, b) -> b.size() - a.size());
            List<DonHang> largestCluster = clusterList.get(0);

            // Chỉ tách nếu cụm lớn hơn gấp đôi số đơn trung bình
            if (largestCluster.size() < avgOrdersPerShipper * 2)
                break;

            // Chia cụm lớn nhất thành 2 cụm nhỏ hơn
            int splitSize = largestCluster.size() / 2;
            List<DonHang> newCluster1 = new ArrayList<>(largestCluster.subList(0, splitSize));
            List<DonHang> newCluster2 = new ArrayList<>(largestCluster.subList(splitSize, largestCluster.size()));

            // Cập nhật danh sách cụm
            clusterList.set(0, newCluster1);
            clusterList.add(newCluster2);
            numClusters++;
        }

        // Đưa danh sách cụm vào Map
        Map<String, List<DonHang>> balancedClusters = new HashMap<>();
        for (int i = 0; i < clusterList.size(); i++) {
            balancedClusters.put("cum" + (i + 1), clusterList.get(i));
        }

        return balancedClusters;
    }

    /**
     * Tạo danh sách đơn hàng giả lập
     */
    public static List<DonHang> generateDonHang() {
        List<DonHang> donHangList = new ArrayList<>();

        // Tạo 9 đơn hàng gần nhau (TPHCM)
        float baseKinhDo = 106.7000f;
        float baseViDo = 10.7769f;

        for (int i = 0; i < 9; i++) {
            float kinhDo = baseKinhDo + (float) (Math.random() * 0.005);
            float viDo = baseViDo + (float) (Math.random() * 0.005);
            donHangList.add(new DonHang(i + 1, kinhDo, viDo));
        }

        donHangList.add(new DonHang(10, baseKinhDo + 1.0f, baseViDo + 1.0f));

        float vungTauKinhDo = 107.0842f;
        float vungTauViDo = 10.3460f;
        donHangList.add(new DonHang(11, vungTauKinhDo, vungTauViDo));
        donHangList.add(new DonHang(12, vungTauKinhDo, vungTauViDo));

        return donHangList;
    }

    public static void printClusters(Map<String, List<DonHang>> clusters) {
        for (Map.Entry<String, List<DonHang>> entry : clusters.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size() + " đơn hàng");
            for (DonHang donHang : entry.getValue()) {
                System.out.println("  - Đơn hàng " + donHang.getId() + ": (" + donHang.getKinhDo() + ", "
                        + donHang.getViDo() + ")");
            }
        }
    }
}
