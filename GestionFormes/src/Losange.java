// Auteur : Ahmed Boukra Bettayeb
// class Losange
// date : 2025-11-25

import java.util.Scanner;

public class Losange extends Forme {
    private static int compteur = 0;

    private double d1, d2, cote, angleRad;
    private double surface, perimetre;

    public Losange() {
        super();
        incrementerCompteur();
        Forme.ajouterAListe(this);
        saisirDimensions();
        System.out.println("Creation d'un Losange. Compteur Losange : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }

    public Losange(boolean fromDiagonales) {
        super();
        incrementerCompteur();
        Forme.ajouterAListe(this);
        saisirDimensions();
        System.out.println("Creation d'un Losange. Compteur Losange : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }

    public static void incrementerCompteur() { compteur++; }
    public static void decrementerCompteur() { if (compteur > 0) compteur--; }

    @Override
    public void saisirDimensions() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Voulez-vous saisir les diagonales ? (o/n)");
            String rep = sc.next();
            if (rep.equalsIgnoreCase("o")) {
                do {
                    System.out.print("d1 (>0) : ");
                    d1 = sc.nextDouble();
                } while (d1 <= 0);
                do {
                    System.out.print("d2 (>0) : ");
                    d2 = sc.nextDouble();
                } while (d2 <= 0);
                cote = Math.sqrt(Math.pow(d1/2.0,2) + Math.pow(d2/2.0,2));
                surface = (d1 * d2) / 2.0;
                perimetre = 4 * cote;
            } else {
                do {
                    System.out.print("cote (>0) : ");
                    cote = sc.nextDouble();
                } while (cote <= 0);
                double angleDeg;
                do {
                    System.out.print("angle deg (0<angle<180) : ");
                    angleDeg = sc.nextDouble();
                } while (angleDeg <= 0 || angleDeg >= 180);
                angleRad = Math.toRadians(angleDeg);
                d1 = 2 * cote * Math.sin(angleRad / 2.0);
                d2 = 2 * cote * Math.cos(angleRad / 2.0);
                surface = cote * cote * Math.sin(angleRad);
                perimetre = 4 * cote;
            }
        }
    }

    @Override
    public double getSurface() { return surface; }

    @Override
    public double getPerimetre() { return perimetre; }

    @Override
    public void detruire() {
        decrementerCompteur();
        detruireFormeGlobale();
        System.out.println("Suppression d'un Losange. Restants Losange : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }
}
