// Auteur : Ahmed Boukra Bettayeb
// GestionFormes - Menu principal 
// Version: 1.0
// Date : 2025-11-25

import java.util.Scanner;

public class GestionFormes {
    private static final int TAILLE_MAX = 100;
    private static final int TAILLE_PAR_TYPE = 10;
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choix = -1;

        while (choix != 0) {
            afficherMenu();
            choix = lireInt(sc);
            switch (choix) {
                case 1: ajouterForme(sc, "Rectangle"); break;
                case 2: ajouterForme(sc, "Carre"); break;
                case 3: ajouterForme(sc, "Ellipse"); break;
                case 4: ajouterForme(sc, "Cercle"); break;
                case 5: ajouterForme(sc, "Losange"); break;
                case 6: ajouterForme(sc, "Triangle"); break;
                case 7: ajouterForme(sc, "Hexagone"); break;
                case 8: supprimerForme(sc); break;
                case 9: afficherTableau(); break;
                case 10: afficherCompteurs(); break;
                case 0: System.out.println("Au revoir."); break;
                default: System.out.println("Choix invalide."); break;
            }
            
        }
            sc.close();
    }

    private static void afficherMenu() {
        System.out.println("\n======= MENU DES FORMES =======");
        System.out.println("1 Rectangle");
        System.out.println("2 Carre");
        System.out.println("3 Ellipse");
        System.out.println("4 Cercle");
        System.out.println("5 Losange");
        System.out.println("6 Triangle quelconque");
        System.out.println("7 Hexagone irregulier");
        System.out.println("8 Supprimer une forme (par index)");
        System.out.println("9 Afficher tableau");
        System.out.println("10 Afficher compteurs");
        System.out.println("0 Quitter");
        System.out.print("Votre choix : ");
    }

    private static int lireInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            sc.next(); // skip
            System.out.print("Entrez un entier : ");
        }
        return sc.nextInt();
    }

    private static void ajouterForme(Scanner sc, String type) {
        Forme f = null;
        try {
            switch (type) {
                case "Rectangle":
                    System.out.print("Longueur = "); double L = sc.nextDouble();
                    System.out.print("Largeur = "); double l = sc.nextDouble();
                    f = new Rectangle(L, l);
                    break;
                case "Carre":
                    System.out.print("Cote = "); double cote = sc.nextDouble();
                    f = new Carre(cote);
                    break;
                case "Ellipse":
                    System.out.print("Grand axe = "); double ga = sc.nextDouble();
                    System.out.print("Petit axe = "); double pa = sc.nextDouble();
                    f = new ellipse(ga, pa);
                    break;
                case "Cercle":
                    System.out.print("Rayon = "); double r = sc.nextDouble();
                    f = new cercle(r);
                    break;
                case "Losange":
                    f = new Losange();
                    break;
                case "Triangle":
                    System.out.println("1: 3 cotes  2: 2 cotes + angle");
                    int t = lireInt(sc);
                    if (t == 1) {
                        f = new TriangleQuelconque();
                    } else {
                        System.out.print("a = "); double a = sc.nextDouble();
                        System.out.print("b = "); double b = sc.nextDouble();
                        System.out.print("angle (deg) = "); double ang = sc.nextDouble();
                        double c3 = Math.sqrt(Math.max(0.0, a*a + b*b - 2*a*b*Math.cos(Math.toRadians(ang))));
                        f = new TriangleQuelconque(a, b, c3);
                    }
                    break;
                case "Hexagone":
                    f = new HexagoneIrregulier();
                    break;
                default:
                    System.out.println("Type inconnu");
            }
        } catch (Exception ex) {
            System.out.println("Erreur lors de la creation/saisie : " + ex.getMessage());
            if (f != null) f.detruire();
            return;
        }
        if (f != null) {
            System.out.printf("Forme ajoutee. Surface=%.3f Perimetre=%.3f\n", f.getSurface(), f.getPerimetre());
        }
    }

    private static void supprimerForme(Scanner sc) {
        System.out.println("Indice de la forme a supprimer (0.." + (Forme.getTailleListe()-1) + ") : ");
        int idx = lireInt(sc);
        if (idx < 0 || idx >= Forme.getTailleListe()) { System.out.println("Indice invalide."); return; }
        Forme f = Forme.getFormeAt(idx);
        if (f == null) { System.out.println("Aucune forme a cet index."); return; }
        f.detruire();
        Forme.retirerDeListe(f);
        System.out.println("Forme supprime.");
    }

    private static void afficherTableau() {
        System.out.println("\n--- Contenu (liste centralisee) ---");
        java.util.List<Forme> liste = Forme.getListeForme();
        if (liste.isEmpty()) {
            System.out.println("  (vide)");
            return;
        }
        for (int i = 0; i < liste.size(); i++) {
            Forme f = liste.get(i);
            String type = f.getClass().getSimpleName();
            System.out.printf("  %d : %s | Surface=%.3f | Perimetre=%.3f\n", i, type, f.getSurface(), f.getPerimetre());
        }
    }

    private static void afficherCompteurs() {
        int rect = 0, carre = 0, ell = 0, cer = 0, los = 0, tri = 0, hex = 0;
        for (Forme f : Forme.getListeForme()) {
            if (f instanceof Rectangle && !(f instanceof Carre)) rect++; // avoid counting Carre as Rectangle
            else if (f instanceof Carre) carre++;
            else if (f instanceof ellipse) ell++;
            else if (f instanceof cercle) cer++;
            else if (f instanceof Losange) los++;
            else if (f instanceof TriangleQuelconque) tri++;
            else if (f instanceof HexagoneIrregulier) hex++;
        }
        System.out.println("=== Compteurs (par type via liste centralisee) ===");
        System.out.printf("Rectangles=%d, Carres=%d, Ellipses=%d, Cercles=%d, Losanges=%d, Triangles=%d, Hexagones=%d\n",
                rect, carre, ell, cer, los, tri, hex);
        System.out.println("Total formes (global) = " + Forme.getCompteurFormes());
    }
}
