package com.ahhf.chen.code;

import lombok.Data;

@Data
public class QrCodeText {

	private String content;
	private int x;
	private int y;

	public QrCodeText() {
	}

	public QrCodeText(String content, int x, int y) {
		super();
		this.content = content;
		this.x = x;
		this.y = y;
	}

}
