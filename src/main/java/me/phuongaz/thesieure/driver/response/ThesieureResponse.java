package me.phuongaz.thesieure.driver.response;

import me.phuongaz.thesieure.card.Card;

public class ThesieureResponse extends Response{

    private String response;

    public ThesieureResponse(String request, Card card) {
        super(request, card);
    }

    @Override
    public String errorToString() {
        int code = this.getResponseCode();
        switch(code) {
            case 1:
                return "§aThẻ thành công đúng mệnh giá";
            case 2:
                return "§cThẻ thành công sai mệnh giá";
            case 3:
                return "§cThẻ lỗi";
            case 4:
                return "§cHệ thống bảo trì";
            case 99:
                return "§eThẻ đang xử lý vui lòng chờ ít phút";
            case 100:
                return "Gửi thẻ thất bại:§c " + this.getResponseMessage();
            default:
                return "§cLỗi: UNKNOWN";
        }
    }

    @Override
    public boolean isSuccess() {
        return this.getResponseCode() == 1 || this.getResponseCode() == 2;
    }

    @Override
    public boolean isPending() {
        return this.getResponseCode() == 99;
    }
}
