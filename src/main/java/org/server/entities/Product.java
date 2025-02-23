package org.server.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "image_url", length = 255)
	private String imageUrl;

	@Column(length = 50)
	private String category;

	@Column(length = 50)
	private String brand;

	@Column(nullable = false)
	private int quantity;

	@Column(columnDefinition = "TEXT")
	private String description;

	public Product() {
	}

	public Product(String name, BigDecimal price, String imageUrl, String category, String brand, String description,
			int quantity) {
		this.name = name;
		this.price = price;
		this.imageUrl = imageUrl;
		this.category = category;
		this.brand = brand;
		this.quantity = quantity;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		if (price.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Giá sản phẩm không thể nhỏ hơn 0.");
		}
		this.price = price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Số lượng không thể nhỏ hơn 0.");
		}
		this.quantity = quantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return String.format(
				"Product{id=%d, name='%s', price=%.2f, imageUrl='%s', category='%s', brand='%s', quantity=%d, description='%s'}",
				id, name, price, imageUrl, category, brand, quantity, description);
	}
}
