// Auteur : Ahmed Boukra Bettayeb
// class Carre
// date : 2025-11-25

import java.util.Scanner;

public class Carre extends Rectangle {
    private static int compteurCarre = 0;

    public Carre(double cote) {
        super(cote, cote);
        incrementerCompteur();
        Forme.ajouterAListe(this);
        System.out.println("Creation d'un Carre. Compteur Carre : " + compteurCarre +
                           ", total formes : " + Forme.getCompteurFormes());
    }

    public Carre() {
        this(1.0);
    }

    public static void incrementerCompteur() { compteurCarre++; }
    public static void decrementerCompteur() { if (compteurCarre > 0) compteurCarre--; }
    public static int getCompteurCarre() { return compteurCarre; }

    @Override
    public void saisirDimensions() {
        try (Scanner sc = new Scanner(System.in)) {
            double c;
            do {
                System.out.print("Entrez le cote du carre (>0) : ");
                c = sc.nextDouble();
            } while (c <= 0);
            setLongueur(c);
            setLargeur(c);
        }
    }

    @Override
    public void saisirDimensions(double a) {
        setLongueur(a);
        setLargeur(a);
    }

    @Override
    public void saisirDimensions(double a, double b) {
        saisirDimensions(a);
    }

    @Override
    public void saisirDimensions(double a, double b, double c) {
    }

    @Override
    public void detruire() {
        decrementerCompteur();
        detruireFormeGlobale();
        System.out.println("Suppression d'un Carre. Restants Carre : " + compteurCarre +
                           ", total formes : " + Forme.getCompteurFormes());
    }
}
