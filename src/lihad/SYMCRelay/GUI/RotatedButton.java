package lihad.SYMCRelay.GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JToggleButton;

public class RotatedButton extends JButton {

	private static final long serialVersionUID = -6269186366137258735L;
	
	XButton template;
	boolean clockwise;

	public RotatedButton(String text, boolean clockwise) {
		template = new XButton(text);
		this.clockwise = clockwise;

		Dimension d = template.getPreferredSize();
		setPreferredSize(new Dimension(d.height, d.width));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		Dimension d = getSize();
		template.setSize(d.height, d.width);

		if (clockwise) {
			g2.rotate(Math.PI / 2.0);
			g2.translate(0, -getSize().width);
		} else {
			g2.translate(0, getSize().height);
			g2.rotate(- Math.PI / 2.0);
		}
		template.setSelected(this.getModel().isPressed());
		template.paintComponent(g2);
		g2.dispose();
	}

	private class XButton extends JToggleButton {
	
		private static final long serialVersionUID = 9222232387484118476L;

		XButton(String text) {
			super(text);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}
}