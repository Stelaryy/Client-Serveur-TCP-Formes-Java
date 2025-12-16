package com.example.applicationtcp;

import java.util.Locale;

public class ShapePayload {
    public final String type;   // rectangle | carre | cercle
    public final double a;      // largeur/cote/rayon
    public final double b;      // hauteur (rectangle uniquement)

    public ShapePayload(String type, double a, double b) {
        this.type = type == null ? "" : type.trim();
        this.a = a;
        this.b = b;
    }

    public String toLine() {
        String shape = type.toUpperCase(Locale.US);
        StringBuilder sb = new StringBuilder();
        sb.append("TYPE=").append(shape);
        sb.append(";A=").append(fmt(a));
        if ("RECTANGLE".equals(shape)) {
            sb.append(";B=").append(fmt(b));
        } else if ("CERCLE".equals(shape)) {
            sb.append(";R=").append(fmt(a)); // rayon
        }
        sb.append("\n");
        return sb.toString();
    }

    private String fmt(double value) {
        return String.format(Locale.US, "%.3f", value);
    }
}
