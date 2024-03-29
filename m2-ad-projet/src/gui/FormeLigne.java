package gui;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Implementation de Forme pour le dessin d'une ligne.
 */
@SuppressWarnings("serial")
public class FormeLigne extends Forme{

	/**
	 * Constructeur.
	 * 
	 * @param bg
	 *            L'arriere plan.
	 * @param fg
	 *            L'avant plan.
	 * @param trait
	 *            L'épaisseur du trait.
	 */
	public FormeLigne(Color bg, Color fg, float trait) {
		super(bg, fg, trait);
	}

	/**
	 * Méthode de dessin de l'arriere plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineArrierePlan(Graphics2D g) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Méthode de dessin de l'avant plan.
	 * 
	 * @param g
	 *            Le contexte de le dessin.
	 */
	public void dessineAvantPlan(Graphics2D g) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Retourne vrai si cette forme est définit par 2 points, faux pour un
	 * point.
	 * 
	 * @return vrai si cette forme est définit par 2 points, faux pour un point.
	 */
	public boolean aDeuxPoints() {
		return true;
	}

	/* (non-Javadoc)
	 * @see gui.Forme#toString()
	 */
	@Override
	public String toString() {
		return "ligne";
	}
}
