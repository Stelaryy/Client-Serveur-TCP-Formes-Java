// Auteur : Ahmed Boukra Bettayeb
// class Rectangle
// date : 2025-11-25

import java.util.Scanner;

public class Rectangle extends Forme {
    private static int compteur = 0;

    protected double longueur;
    protected double largeur;
    protected double surface;
    protected double perimetre;

    public Rectangle(double longueur, double largeur) {
        super(); // incrémente compteur global
        // n'ajoute à la liste que si l'instance est exactement Rectangle (pas pour les sous-classes)
        if (this.getClass() == Rectangle.class) {
            incrementerCompteur();
            Forme.ajouterAListe(this);
            System.out.println("Creation d'un " + this.getClass().getSimpleName() + ". Compteur Rectangle : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
        }
        setLongueur(longueur);
        setLargeur(largeur);
    }

    public Rectangle() {
        this(1.0, 1.0);
    }

    // Compteurs statiques
    public static void incrementerCompteur() { compteur++; }
    public static void decrementerCompteur() { if (compteur>0) compteur--; }
    public static int getCompteurRectangle() { return compteur; }

    // setters protégés
    protected void setLongueur(double L) {
        if (L <= 0) L = 1.0;
        this.longueur = L;
        calculerSurface();
        calculerPerimetre();
    }

    protected void setLargeur(double l) {
        if (l <= 0) l = 1.0;
        this.largeur = l;
        calculerSurface();
        calculerPerimetre();
    }

    protected void calculerSurface() {
        this.surface = longueur * largeur;
    }

    protected void calculerPerimetre() {
        this.perimetre = 2 * (longueur + largeur);
    }

    // saisies interactives
    @Override
    public void saisirDimensions() {
        Scanner sc = new Scanner(System.in);
        double L, l;
        do {
            System.out.print("Entrez la longueur (>0) : ");
            L = sc.nextDouble();
        } while (L <= 0);
        do {
            System.out.print("Entrez la largeur (>0) : ");
            l = sc.nextDouble();
        } while (l <= 0);
        setLongueur(L);
        setLargeur(l);
    }
    public void saisirDimensions(double a, double b) {
        setLongueur(a);
        setLargeur(b);
    }

    @Override
    public double getSurface() { return surface; }

    @Override
    public double getPerimetre() { return perimetre; }

    // suppression manuelle
    public void detruire() {
        decrementerCompteur();
        detruireFormeGlobale();
        System.out.println("Suppression d'un Rectangle. Restants Rectangle : " + compteur +
                           ", total formes : " + Forme.getCompteurFormes());
    }
}
