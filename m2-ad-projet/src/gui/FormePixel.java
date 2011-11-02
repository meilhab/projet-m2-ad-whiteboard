package gui;

import java.awt.Color;
import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class FormePixel extends Forme {

	public FormePixel(Color bg, Color fg, float trait) {
		super(bg, fg, trait);

	}

	@Override
	public void dessineArrierePlan(Graphics2D g) {
		g.fillRect(p1.x, p1.y, 1, 1);
	}

	@Override
	public void dessineAvantPlan(Graphics2D g) {
		g.drawRect(p1.x, p1.y, 1, 1);
	}

	@Override
	public boolean aDeuxPoints() {
		return false;
	}

	/* (non-Javadoc)
	 * @see gui.Forme#toString()
	 */
	@Override
	public String toString() {
		return "point";
	}
}
