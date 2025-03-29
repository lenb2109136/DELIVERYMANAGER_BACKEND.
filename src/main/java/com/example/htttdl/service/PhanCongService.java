package com.example.htttdl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.htttdl.DTO.Customer.Admin.PhanCongDTO;
import com.example.htttdl.config.AminBean;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.repository.OrderRepository;

@Service
public class PhanCongService {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepo;

    AminBean a = new AminBean(1);

    public List<NHANVIEN> checkShiper(List<Integer> shiperIds, Integer diemNhanHangId) {
        return orderRepo.getAllShiperInListAndOfBuuCuc(shiperIds, diemNhanHangId);
    }

    public List<NHANVIEN> getAllShipper(Integer diemNhanHangId) {
        return orderRepo.getAllShiperOfBuuCuc(diemNhanHangId);
    }

    public List<DonHang> getOrderTop(Integer buuCucId, Integer top) {
        return orderRepo.getTopOrderByBuuCuc(buuCucId, PageRequest.of(0, top));
    }

    public List<DonHang> getOrderTopLayHang(Integer buuCucId, Integer top) {
        return orderRepo.getTopOrderLayHangByBuuCuc(buuCucId, PageRequest.of(0, top));
    }

    public Map<String, PhanCongDTO> generateShiper(List<DonHang> orders, List<NHANVIEN> shippers) {
        Map<String, List<DonHang>> mapData = clusterDonHang(orders, 0.0901, 0);
        mapData = balanceClusters(mapData, shippers.size(), orders.size());
        return phanCongTuyenTinh(mapData, shippers);
    }

    public Map<String, PhanCongDTO> generateShiperLayHang(List<DonHang> orders, List<NHANVIEN> shippers) {
        Map<String, List<DonHang>> mapData = clusterDonHang(orders, 0.0901, 0);
        mapData = balanceClusters(mapData, shippers.size(), orders.size());

        return phanCongTuyenTinh(mapData, shippers);
    }

    public Map<String, PhanCongDTO> phanCongTuyenTinh(
            Map<String, List<DonHang>> clusteredOrders,
            List<NHANVIEN> shippers) {

        Map<String, PhanCongDTO> assignmentMap = new HashMap<>();

        // Danh sách cụm đơn hàng chưa được gán
        List<Map.Entry<String, List<DonHang>>> unassignedClusters = new ArrayList<>(clusteredOrders.entrySet());
        System.out.println("=================================================================");
        System.out.println("SO CUM CAN GAN LA: " + unassignedClusters.size());
        Integer a = 0;
        Integer b = unassignedClusters.size();
        while (a != b) {
            List<NHANVIEN> availableShippers = new ArrayList<>(shippers);
            List<Map.Entry<String, List<DonHang>>> remainingClusters = new ArrayList<>();
            for (Map.Entry<String, List<DonHang>> entry : unassignedClusters) {
                List<DonHang> orders = entry.getValue();

                if (availableShippers.isEmpty()) {
                    remainingClusters.add(entry); // Nếu hết shipper, cụm này sẽ chờ vòng lặp tiếp theo
                    continue;
                }

                // Tính trung tâm cụm
                double[] center = tinhTrungTamCum(orders);

                // Tìm shipper gần nhất
                NHANVIEN nearestShipper = null;
                double minDistance = Double.MAX_VALUE;

                for (NHANVIEN shipper : availableShippers) {
                    double distance = haversine(shipper.getViDo(), shipper.getKinhDo(), center[1], center[0]);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestShipper = shipper;
                    }
                }

                if (nearestShipper != null) {
                    if (assignmentMap.get(nearestShipper.getId().toString()) == null) {
                        System.out.println("Vô ddaay ");
                        assignmentMap.put(nearestShipper.getId().toString(),
                                new PhanCongDTO(orders, nearestShipper, minDistance));
                    } else {
                        System.out.println("Vô đây");
                        PhanCongDTO p = assignmentMap.get(nearestShipper.getId().toString());
                        p.getOrders().addAll(orders);
                    }

                    a++;
                    availableShippers.remove(nearestShipper);
                } else {
                    remainingClusters.add(entry); // Nếu không tìm thấy shipper, giữ lại cụm này
                }
            }

            // Cập nhật danh sách cụm chưa được gán
            unassignedClusters = remainingClusters;

            // Nếu sau một vòng lặp mà không có cụm nào được gán => dừng
            if (a >= b) {
                break;
            }
        }

        return assignmentMap;
    }

    public static Map<String, List<DonHang>> clusterDonHang(List<DonHang> donHangList, double eps, int minPts) {
        List<DoublePoint> points = new ArrayList<>();
        Map<DoublePoint, List<DonHang>> pointToDonHangs = new HashMap<>();
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

    public static Map<String, List<DonHang>> clusterDonHangLayHang(List<DonHang> donHangList, double eps, int minPts) {
        List<DoublePoint> points = new ArrayList<>();
        Map<DoublePoint, List<DonHang>> pointToDonHangs = new HashMap<>();
        for (DonHang donHang : donHangList) {
            double[] coordinates = { donHang.getXa().getKinhDo(), donHang.getXa().getViDo() };
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
    public static Map<String, List<DonHang>> balanceClusters(Map<String, List<DonHang>> clusters, int numShippers,
            Integer orderSize) {
        if (orderSize < numShippers) {
            return clusters;
        }
        List<List<DonHang>> clusterList = new ArrayList<>(clusters.values());
        int totalOrders = clusterList.stream().mapToInt(List::size).sum();
        int avgOrdersPerShipper = totalOrders / numShippers;
        int numClusters = clusterList.size();
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

    // Tính trung tâm cụm dựa trên trung bình tọa độ
    private static double[] tinhTrungTamCum(List<DonHang> orders) {
        double sumLat = 0, sumLon = 0;
        for (DonHang order : orders) {
            sumLon += order.getKinhDo();
            sumLat += order.getViDo();
        }
        return new double[] { sumLon / orders.size(), sumLat / orders.size() };
    }

    // Hàm tính khoảng cách giữa hai tọa độ theo Haversine
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính trái đất (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}