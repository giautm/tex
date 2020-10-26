# Tex
T-Express

# Cấu trúc dữ liệu json cho Tex

## Danh sách nhân viên vận chuyển

```js
"deliveryMan": {
    "$user-id": {
        "name": String,           // Tên nhân viên giao nhận
        "phone": String,          // Số điện thoại
        "isActive": Boolean,
        "tikiId": String,         // ID của nhân viên trên Tiki's CPN.
        "latLng": [Number,Number] // [Vĩ độ, Kinh độ] hiện tại.
        "latLngUpdateAt": Number  // TIMESTAMP cập nhật vị trí.
    }
}
```

## Danh sách các đơn hàng

```js
"orders": {
    "$order-code": {
        "orderCode": String,       // Mã đơn hàng
        "thirdParty": String,      // Đối tác vận chuyển
        "shippingCode": String,    // Mã vận chuyển
        "recipientName": String,   // Tên người nhận hàng
        "recipientPhone": String,  // Số điện thoại người nhận
        "deliveryAddress": String, // Địa chỉ giao hàng
        "totalAmount": Number,     // Giá trị đơn hàng
        "receivables": Number,     // Các khoản phải thu
        "status": String,          // Trạng thái hiện tại
        "updateAt": String         // TIMESTAMP cập nhật thông tin
    }
}
```

## Chuyến đi giao của mỗi nhân viên vận chuyển

```js
"transport": {
    "$user-id": {
        "current": { // Danh sách những đơn hàng mới, chờ đi giao.
            "$order-code": {
                "orderCode": String,     // Mã đơn hàng
                "createdAt": Number,     // TIMESTAMP tạo chuyến đi giao
                "createdBy": String,     // Id của NVVC điều phối
                "callHistory": [Number], // Mảng TIMESTAMP các cuộc gọi.
                "sendHistory": [Number]  // Mảng TIMESTAMP các tin nhắn.
            }
        },
        "updated": { // Danh sách những đơn hàng đã cập nhật, giao thành công hoặc thất bại.
            "$order-code": {
                "orderCode": String,            // Mã đơn hàng
                "createdAt": Number,            // TIMESTAMP tạo chuyến đi giao
                "createdBy": String,            // Id của NVVC điều phối
                "callHistory": [Number],        // Mảng TIMESTAMP các cuộc gọi.
                "sendHistory": [Number],        // Mảng TIMESTAMP các tin nhắn.

                "cause": Number,                // Lý do trả về, 1 - Giao thành công.
                "causeNote": String,            // Ghi chú cho lý do.
                "updatedAt": Number,            // TIMESTAMP cập nhật.
                "latLngUpdate": [Number,Number] // [Vĩ độ, Kinh độ] Vị trí cập nhật thông tin
            }
        },
        "success": { // Lưu giữ thông tin những đơn hàng giao thành công theo từng ngày.
            "$yyyy-MM-dd": {
                "$array-id": {
                    "orderCode": String,             // Mã đơn hàng
                    "createdAt": Number,             // TIMESTAMP tạo chuyến đi giao
                    "createdBy": String,             // Id của NVVC điều phối
                    "callHistory": [Number],         // Mảng TIMESTAMP các cuộc gọi.
                    "sendHistory": [Number],         // Mảng TIMESTAMP các tin nhắn.

                    "cause": Number,                 // Giá trị 1 - Giao thành công.
                    "causeNote": String,             // Ghi chú cho lý do.
                    "updatedAt": Number,             // TIMESTAMP cập nhật.
                    "latLngUpdate": [Number,Number], // [Vĩ độ, Kinh độ] Vị trí cập nhật thông tin

                    "confirmAt": Number,             // TIMESTAMP điều phối xác nhận.
                    "confirmBy": String              // ID của điều phối xác nhận.
                }
            }
        },
        "archive": { // Lưu giữ thông tin những đơn hàng giao thất bại theo từng ngày.
            "$yyyy-MM-dd": {
                "$array-id": {
                    "orderCode": String,             // Mã đơn hàng
                    "createdAt": Number,             // TIMESTAMP tạo chuyến đi giao
                    "createdBy": String,             // Id của NVVC điều phối
                    "callHistory": [Number],         // Mảng TIMESTAMP các cuộc gọi.
                    "sendHistory": [Number],         // Mảng TIMESTAMP các tin nhắn.

                    "cause": Number,                 // Lý do trả về.
                    "causeNote": String,             // Ghi chú cho lý do.
                    "updatedAt": Number,             // TIMESTAMP cập nhật.
                    "latLngUpdate": [Number,Number], // [Vĩ độ, Kinh độ] Vị trí cập nhật thông tin

                    "confirmAt": Number,             // TIMESTAMP điều phối xác nhận.
                    "confirmBy": String              // ID của điều phối xác nhận.
                }
            }
        }
    }
}
```
