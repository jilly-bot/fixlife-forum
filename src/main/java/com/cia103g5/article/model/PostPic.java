package com.cia103g5.article.model;

import jakarta.persistence.*;

@Entity
@Table(name = "post_pic") // 對應資料庫中的 post_pic 表格
public class PostPic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵自動生成
    private int picNo; // 圖片編號 (主鍵)

    @Column(name = "post_no", nullable = false) // 關聯到文章編號
    private int postNo; // 發文編號

    @Lob
    @Column(nullable = false) // 將圖片資料儲存為二進制大物件
    private byte[] pic; // 圖片資料

    // Getters 和 Setters
    public int getPicNo() {
        return picNo;
    }

    public void setPicNo(int picNo) {
        this.picNo = picNo;
    }

    public int getPostNo() {
        return postNo;
    }

    public void setPostNo(int postNo) {
        this.postNo = postNo;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }
}
