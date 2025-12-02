// Auteur : Ahmed Boukra Bettayeb
// class Ellipse
// date : 2025-11-25

import java.util.Scanner;

public class ellipse extends Forme {
    private static int compteur = 0;

    protected double grandAxe;
    protected double petitAxe;
    protected double surface;
    protected double perimetre;

    public ellipse(double grandAxe, double petitAxe) {
        super();
        if (grandAxe <= 0 || petitAxe <= 0) throw new IllegalArgumentException("Axes>0");
        this.grandAxe = grandAxe;
        this.petitAxe = petitAxe;
        if (this.getClass() == ellipse.class) {
            incrementerCompteur();
            Forme.ajouterAListe(this);
        }
        calculerSurface();
        calculerPerimetre();
        if (this.getClass() == ellipse.class) {
            System.out.println("Creation d'une " + this.getClass().getSimpleName() + ". Compteur Ellipse : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
        }
    }

    public ellipse() { this(1.0, 1.0); }

    public static void incrementerCompteur() { compteur++; }
    public static void decrementerCompteur() { if (compteur > 0) compteur--; }
    public static int getCompteurEllipse() { return compteur; }

    protected void calculerSurface() { surface = Math.PI * grandAxe * petitAxe; }

    protected void calculerPerimetre() {
        perimetre = Math.PI * (3*(grandAxe + petitAxe) - Math.sqrt((3*grandAxe + petitAxe)*(grandAxe + 3*petitAxe)));
    }

    @Override
    public void saisirDimensions() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Entrez le grand axe : ");
            double a = sc.nextDouble();
            System.out.print("Entrez le petit axe : ");
            double b = sc.nextDouble();
            if (a <= 0 || b <= 0) throw new IllegalArgumentException("Axes>0");
            this.grandAxe = a; this.petitAxe = b;
            calculerSurface(); calculerPerimetre();
        }
    }

    @Override
    public void saisirDimensions(double a, double b) {
        this.grandAxe = a; this.petitAxe = b;
        calculerSurface(); calculerPerimetre();
    }

    @Override
    public double getSurface() { return surface; }

    @Override
    public double getPerimetre() { return perimetre; }

    @Override
    public void detruire() {
        decrementerCompteur();
        detruireFormeGlobale();
        System.out.println("Suppression d'une Ellipse. Restants Ellipse : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }
}
