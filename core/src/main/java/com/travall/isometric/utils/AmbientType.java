package com.travall.isometric.utils;

public enum AmbientType {
	NONE(1), DARKEN(0), FULLBRIGHT(1);

	public final int value;

	private AmbientType(int value) {
		this.value = value;
	}
}
