package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.tree.DefaultMutableTreeNode;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeCellRenderer;
import com.alee.laf.tree.WebTreeModel;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;
import lihad.SYMCRelay.GUI.RotatedButton;

public class UserPane extends WebPanel {

	private static final long serialVersionUID = -8395492876472798137L;
	private WebPanel userPane;
	private WebTree webTree;
	private DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("SYMCRelay User List");

	//public WebList getUserList(){ return webList; }

	public void updateNodes(String string) {		
		DefaultMutableTreeNode channel = null;
		DefaultMutableTreeNode user = null;
		
		top.removeAllChildren();

		String[] elements = string.split("\n");
		for(int i = 0; i<elements.length; i++){
			if(elements[i].contains("#")){
				channel = new DefaultMutableTreeNode(elements[i]);
				top.add(channel);
			}else{
				if(channel != null){
					user = new DefaultMutableTreeNode(elements[i]);
					channel.add(user);
				}
			}
		}
		WebTreeModel model = (WebTreeModel) (webTree.getModel()); 
		model.reload(); 
		webTree.expandNode(top);
	}



	public UserPane(){
		super(new BorderLayout());
		userPane = new WebPanel(new BorderLayout());

		webTree = new WebTree(top);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setOpenIcon(null);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setClosedIcon(null);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setLeafIcon(null);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setRootIcon(null);


		WebScrollPane scrollPane = new WebScrollPane(webTree);
		scrollPane.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		userPane.add(scrollPane, BorderLayout.CENTER);
		userPane.setPreferredSize(new Dimension(175, 200));
		userPane.setVisible(Client.getRelayConfiguration().getUserListExpanded());
		scrollPane.getVerticalScrollBar().setUnitIncrement(32);



		final RotatedButton expandButton = new RotatedButton("expand user list", false);
		final WebButton shrinkButton = new WebButton("shrink user list");

		shrinkButton.setVisible(Client.getRelayConfiguration().getUserListExpanded());
		expandButton.setVisible(!Client.getRelayConfiguration().getUserListExpanded());

		ActionAdapter expandButtonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				expandButton.setVisible(false);
				shrinkButton.setVisible(true);
				userPane.setVisible(true);
				Client.getRelayConfiguration().setUserListExpanded(true);
			}
		};
		ActionAdapter shrinkButtonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				expandButton.setVisible(true);
				shrinkButton.setVisible(false);
				userPane.setVisible(false);
				Client.getRelayConfiguration().setUserListExpanded(false);
			}
		};

		expandButton.addActionListener(expandButtonListener);
		shrinkButton.addActionListener(shrinkButtonListener);

		this.add(userPane, BorderLayout.EAST);
		this.add(expandButton, BorderLayout.WEST);
		this.add(shrinkButton, BorderLayout.SOUTH);
	}
}