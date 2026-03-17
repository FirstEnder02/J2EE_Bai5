package J2EE_Bai5.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên sách không được để trống")
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull(message = "Giá sách không được để trống")
    @PositiveOrZero(message = "Giá sách phải lớn hơn hoặc bằng 0")
    @Column(nullable = false)
    private Long price;

    @Length(min = 0, max = 200, message = "Tên hình ảnh không quá 200 kí tự")
    @Column(length = 200)
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}