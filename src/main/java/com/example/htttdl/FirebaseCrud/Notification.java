package com.example.htttdl.FirebaseCrud;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notification {

    public static void pushNotifycationBuuCuc(Integer buuCucId, String type, String message, String date, Object data) {
        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("NotifycationAccount/PickUpPoint/" + buuCucId);
        String newId = database.push().getKey();

        if (newId != null) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("createDate", "2024-03-19/18:00:00");
            newData.put("data", data);
            newData.put("message", message);
            newData.put("type", type);
            newData.put("isReaded", false);

            // Push dữ liệu lên Firebase
            database.child(newId).setValueAsync(newData);
            System.out.println("✅ Dữ liệu đã được push vào: " + newId);
        } else {
            System.out.println("❌ Lỗi: Không tạo được ID mới!");
        }
    }

    public static void pushData() {
        // Lấy reference đến node "Customer/1"
        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("NotifycationAccount/Customer/1");

        // Tạo một ID tự động
        String newId = database.push().getKey();

        if (newId != null) {
            // Dữ liệu mới
            Map<String, Object> newData = new HashMap<>();
            newData.put("createDate", "2024-03-19/18:00:00");
            newData.put("data", 2);
            newData.put("message", "Đơn hàng của bạn đã được giao thành công!");
            newData.put("type", "order");

            // Push dữ liệu lên Firebase
            database.child(newId).setValueAsync(newData);
            System.out.println("✅ Dữ liệu đã được push vào: " + newId);
        } else {
            System.out.println("❌ Lỗi: Không tạo được ID mới!");
        }
    }
}
