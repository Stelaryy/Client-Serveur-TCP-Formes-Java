// Auteur : Ahmed Boukra Bettayeb
// class HexagoneIrregulier
// date : 2025-11-25

import java.util.Scanner;

public class HexagoneIrregulier extends Forme {
    private static int compteur = 0;

    private double[][] sommets = new double[6][2];
    private double surface;
    private double perimetre;

    public HexagoneIrregulier(double[][] sommets) {
        super();
        if (sommets == null || sommets.length != 6) throw new IllegalArgumentException("6 sommets requis");
        for (int i=0;i<6;i++){
            this.sommets[i][0] = sommets[i][0];
            this.sommets[i][1] = sommets[i][1];
        }
        incrementerCompteur();
        Forme.ajouterAListe(this);
        calculerPerimetre();
        calculerSurface();
        System.out.println("Creation d'un Hexagone. Compteur Hexagone : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }

    public HexagoneIrregulier() {
        super();
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Saisie des 6 sommets (x y) :");
            for (int i=0;i<6;i++){
                System.out.print("Sommet " + (i+1) + " x : "); this.sommets[i][0] = sc.nextDouble();
                System.out.print("Sommet " + (i+1) + " y : "); this.sommets[i][1] = sc.nextDouble();
            }
        }
        incrementerCompteur();
        calculerPerimetre();
        calculerSurface();
        System.out.println("Creation d'un Hexagone. Compteur Hexagone : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }

    public static void incrementerCompteur() { compteur++; }
    public static void decrementerCompteur() { if (compteur > 0) compteur--; }

    private void calculerPerimetre() {
        perimetre = 0.0;
        for (int i=0;i<6;i++){
            int j=(i+1)%6;
            double dx = sommets[j][0]-sommets[i][0];
            double dy = sommets[j][1]-sommets[i][1];
            perimetre += Math.sqrt(dx*dx+dy*dy);
        }
    }

    private void calculerSurface() {
        double somme = 0.0;
        for (int i=0;i<6;i++){
            int j=(i+1)%6;
            somme += sommets[i][0]*sommets[j][1] - sommets[j][0]*sommets[i][1];
        }
        surface = Math.abs(somme)/2.0;
    }

    @Override
    public void saisirDimensions() {
        try (Scanner sc = new Scanner(System.in)) {
            for (int i=0;i<6;i++){
                System.out.print("Sommet " + (i+1) + " x : "); sommets[i][0] = sc.nextDouble();
                System.out.print("Sommet " + (i+1) + " y : "); sommets[i][1] = sc.nextDouble();
            }
            calculerPerimetre(); calculerSurface();
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
        System.out.println("Suppression d'un Hexagone. Restants Hexagone : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }
}
