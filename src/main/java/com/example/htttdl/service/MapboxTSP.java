package com.example.htttdl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class MapboxTSP {
    private static final String API_KEY = "pk.eyJ1IjoibGVuYjIxMDkxMzYiLCJhIjoiY203ZGd5ZWRtMDM1bzJrcTFoYzNjbjFobyJ9.S_jGEjD3XG--Rdsv3dYCyQ";

    public static double[][] layMaTranKhoangCach(List<ToaDo> danhSachDiem) throws Exception {
        StringBuilder coordinates = new StringBuilder();
        for (ToaDo diem : danhSachDiem) {
            coordinates.append(diem.kinhDo).append(",").append(diem.viDo).append(";");
        }
        coordinates.deleteCharAt(coordinates.length() - 1);

        String url = "https://api.mapbox.com/directions-matrix/v1/mapbox/driving/" +
                coordinates + "?annotations=distance&access_token=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return parseMatrix(response.body(), danhSachDiem.size());
    }

    private static double[][] parseMatrix(String json, int n) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode distancesNode = rootNode.path("distances");

        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = distancesNode.get(i).get(j).asDouble();
            }
        }
        return matrix;
    }
}
