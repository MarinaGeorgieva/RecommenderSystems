package fmi.recommendersystems.hw1;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class News {

	private String content;
	private String category;
	private String fileName;
	private boolean isRead;
}
