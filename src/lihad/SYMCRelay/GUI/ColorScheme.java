package lihad.SYMCRelay.GUI;

import java.awt.Color;

public enum ColorScheme {
	
	//(tab_selected_color, tab_unselected_color, )
	
	DEFAULT(0xA4ACF0, 0xACACAC);
	
	private Color tab_selected_color, tab_unselected_color;
	
	private ColorScheme(int tab_selected_color, int tab_unselected_color){
		this.tab_selected_color = new Color(tab_selected_color);
		this.tab_unselected_color = new Color(tab_unselected_color);

	}
	
	public Color getTabSelectedColor(){
		return tab_selected_color;
	}
	
	public Color getTabUnselectedColor(){
		return tab_unselected_color;
	}

}
