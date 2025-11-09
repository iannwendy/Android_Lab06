# Hướng dẫn Test Database SQLite

## Tổng quan
Dự án đã được chuyển đổi từ lưu trữ tạm thời sang sử dụng SQLite database để lưu trữ dữ liệu sự kiện.

## Các thay đổi chính

### 1. EventDatabaseHelper.kt
- Tạo class SQLiteOpenHelper để quản lý database
- Schema database với bảng `events`:
  - `id` (TEXT PRIMARY KEY): ID duy nhất của sự kiện
  - `name` (TEXT NOT NULL): Tên sự kiện
  - `place` (TEXT NOT NULL): Địa điểm
  - `date` (INTEGER NOT NULL): Timestamp của ngày
  - `time` (TEXT NOT NULL): Thời gian (HH:mm)
  - `is_enabled` (INTEGER NOT NULL): Trạng thái bật/tắt (1/0)

### 2. EventViewModel.kt
- Cập nhật để sử dụng database thay vì `mutableStateListOf`
- Sử dụng coroutines để xử lý database operations
- Thêm các phương thức test và thống kê

### 3. DatabaseTestActivity.kt
- Activity riêng để test database functionality
- Chạy 11 test cases để kiểm tra tất cả chức năng database

## Cách Test Database

### Phương pháp 1: Sử dụng Database Test Activity
1. Mở ứng dụng
2. Trong màn hình danh sách sự kiện, nhấn vào menu (3 chấm) ở góc phải trên
3. Chọn "Test Database"
4. Nhấn "Run Database Tests"
5. Xem kết quả test

### Phương pháp 2: Sử dụng Database Stats
1. Trong màn hình danh sách sự kiện, nhấn vào menu (3 chấm)
2. Chọn "Database Stats"
3. Xem trạng thái database và thống kê

### Phương pháp 3: Test thủ công
1. **Test thêm sự kiện**: Nhấn nút "+" để thêm sự kiện mới
2. **Test sửa sự kiện**: Long press vào sự kiện và chọn "Edit"
3. **Test xóa sự kiện**: Long press vào sự kiện và chọn "Delete"
4. **Test bật/tắt sự kiện**: Nhấn vào switch của sự kiện
5. **Test lọc sự kiện**: Sử dụng switch "Show all" ở top bar
6. **Test xóa tất cả**: Menu > "Remove all"

## Các Test Cases được thực hiện

1. ✅ **Database connection**: Kiểm tra kết nối database
2. ✅ **Table exists**: Kiểm tra bảng có tồn tại không
3. ✅ **Initial event count**: Đếm số sự kiện ban đầu
4. ✅ **Insert event**: Thêm sự kiện mới
5. ✅ **Updated event count**: Đếm số sự kiện sau khi thêm
6. ✅ **Retrieve all events**: Lấy tất cả sự kiện
7. ✅ **Update event**: Cập nhật sự kiện
8. ✅ **Toggle event status**: Bật/tắt trạng thái sự kiện
9. ✅ **Filtered events**: Lọc sự kiện theo trạng thái
10. ✅ **Delete event**: Xóa sự kiện
11. ✅ **Final event count**: Đếm số sự kiện cuối cùng

## Kiểm tra Log
Để xem chi tiết hoạt động của database, mở Logcat và filter theo tag:
- `EventDatabaseHelper`: Log của database operations
- `DatabaseTest`: Log của test cases

## Database File Location
Database được lưu tại: `/data/data/com.example.lab05_exercise2/databases/events.db`

## Troubleshooting

### Nếu database test fail:
1. Kiểm tra Logcat để xem lỗi cụ thể
2. Đảm bảo ứng dụng có quyền ghi database
3. Thử uninstall và reinstall ứng dụng để reset database

### Nếu không thấy dữ liệu:
1. Kiểm tra database stats để xem số lượng events
2. Thử toggle switch "Show all" để hiển thị tất cả events
3. Restart ứng dụng để reload dữ liệu từ database

## Tính năng mới
- **Persistent storage**: Dữ liệu được lưu trữ vĩnh viễn
- **Database statistics**: Xem thống kê database
- **Comprehensive testing**: Test toàn diện các chức năng database
- **Error handling**: Xử lý lỗi database operations
- **Performance optimization**: Sử dụng coroutines cho database operations
