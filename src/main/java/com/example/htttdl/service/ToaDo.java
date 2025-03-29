package com.example.htttdl.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.example.htttdl.modal.DonHang;

import jakarta.persistence.EntityNotFoundException;

public class ToaDo {
    double viDo, kinhDo;

    public ToaDo(double viDo, double kinhDo) {
        this.viDo = viDo;
        this.kinhDo = kinhDo;
    }

    public Map<Object, Object> TSPKhoangCach(ToaDoDTO toadoDTO) throws Exception {
        List<ToaDo> danhSachDiem = new ArrayList<ToaDo>();
        danhSachDiem.add(toadoDTO.getToaDoGoc());
        if (toadoDTO.getDsdonhang().size() <= 1) {
            throw new EntityNotFoundException("Chỉ có thể lập nếu nhiều hơn một đơn hàng");
        }
        for (int i = 0; i < toadoDTO.getDsdonhang().size(); i++) {
            ToaDo t = new ToaDo(toadoDTO.getDsdonhang().get(i).getXa().getViDo(),
                    toadoDTO.getDsdonhang().get(i).getXa().getKinhDo());
            danhSachDiem.add(t);
        }

        double[][] graph = MapboxTSP.layMaTranKhoangCach(danhSachDiem);
        List<Integer> loTrinhChiSo = TSP.solveTSP(graph, 0);
        List<DonHang> d = new ArrayList<DonHang>();
        System.out.println("số lượng phần tử: " + loTrinhChiSo.size());
        for (int i = 1; i < loTrinhChiSo.size() - 1; i++) {
            int index = loTrinhChiSo.get(i) - 1;
            System.out.println("Chỉ số phần tử : " + index);
            if (index >= 0 && index < toadoDTO.getDsdonhang().size()) {
                DonHang donHang = toadoDTO.getDsdonhang().get(index);
                if (!d.contains(donHang)) {
                    d.add(donHang);
                }
            } else {
                throw new IndexOutOfBoundsException("Chỉ số " + index + " vượt quá danh sách đơn hàng!");
            }
        }

        TSP.bestPath = new ArrayList<>();

        toadoDTO.setDsdonhang(d);
        Map<Object, Object> map = new HashMap<>();
        map.put("tongkhoangcach", TSP.getMinCost());
        map.put("loTrinh", toadoDTO);
        return map;
    }

    @Override
    public String toString() {
        return "(" + viDo + ", " + kinhDo + ")";
    }
    // public static void main(String[] args) throws Exception {
    // List<ToaDo> danhSachDiem = Arrays.asList(
    // new ToaDo(10.776111, 106.700000),
    // new ToaDo(21.028511, 105.854444),
    // new ToaDo(16.047778, 108.220556)
    // );
    //
    // double[][] graph = MapboxTSP.layMaTranKhoangCach(danhSachDiem);
    // List<Integer> lộTrình = TSP.solveTSP(graph, 0);
    // System.out.println("Lộ trình tối ưu: " + lộTrình);
    // System.out.println("Tổng khoảng cách: " + TSP.getMinCost() + " mét");
    //
    // System.out.println("Chi tiết tọa độ theo lộ trình:");
    // for (int i : lộTrình) {
    // System.out.println(danhSachDiem.get(i));
    // }
    //
    // }

    public double getViDo() {
        return viDo;
    }

    public void setViDo(double viDo) {
        this.viDo = viDo;
    }

    public double getKinhDo() {
        return kinhDo;
    }

    public void setKinhDo(double kinhDo) {
        this.kinhDo = kinhDo;
    }

}
