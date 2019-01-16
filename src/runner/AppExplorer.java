package runner;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AppExplorer {
	private static final int WIDTH = 200, HEIGHT = 30;
	private ArrayList<String> apps = new ArrayList<String>();
	private JFrame frame;
	private JTextField textField = new JTextField();
	private JList<String> list;
	private TrayIcon trayIcon;
	private SystemTray tray;
	private Dimension screen;
	private Render render;
	private Image icon;
	private int posX = 0, posY = 0;

	public AppExplorer() {
		installFiles();
		setupWindow();
		setupComponents();
		setWindowAdapters();
	}

	private void installFiles() {
		String username = System.getProperty("user.name");
		File file = new File("C:/Users/" + username + "/AppData/Roaming/Z1Software");
		try {
			icon = (new ImageIcon(getClass().getResource("/images/javaIcon.jpg"))).getImage();
		} catch (Exception e1) {
			System.out.println("Image not found");
			System.exit(0);
			e1.printStackTrace();
		}
		if (file.mkdir()) {
			System.out.println("File sucessfully made");
		}
		if (file.list().length == 0) {
			System.out.println("No applications are installed");
		} else {
			for (String app : file.list()) {
				if (app.contains(".jar")) {
					apps.add(app.replace(".jar", ""));
				} else if (app.contains(".exe")) {
					apps.add(app.replace(".exe", ""));
				} else if (app.contains(".lnk")) {
					apps.add(app.replace(".lnk", ""));
				} else {
					System.out.println("The file type isn't recognized");
					System.exit(0);
				}
			}
		}
		String[] temp = new String[apps.size()];
		for (int i = 0; i < apps.size(); i++)
			temp[i] = apps.get(i);
		list = new JList<String>(temp);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
	}

	private void setupComponents() {
		textField.setPreferredSize(new Dimension(WIDTH - 10, HEIGHT - 10));
		textField.setText("Search app...");
		textField.setOpaque(true);
		textField.setBackground(new Color(240, 240, 240, 40));
		textField.setBorder(null);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (apps.contains(textField.getText())) {
					String username = System.getProperty("user.name");
					String[] command = new String[] { "cmd", "/c",
							"C:\\Users\\" + username + "\\AppData\\Roaming\\Z1Software\\" + textField.getText() + ".lnk" };
					File file = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Z1Software\\" + textField.getText() + ".lnk");
					if (file.exists())
						try {
							new ProcessBuilder(command).start();
						} catch (Exception e1) {
							System.out.println("Exception e1, lnk (shortcut) not found");
						}
					file = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Z1Software\\" + textField.getText() + ".exe");
					if (file.exists())
						try {
							new ProcessBuilder(file.toString()).start();
						} catch (Exception e2) {
							System.out.println("Exception e2, exe not found");
						}
					file = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Z1Software\\" + textField.getText() + ".jar");
					if (file.exists())
						try {
							command = new String[] { "javaw", "-jar", file.toString() };
							new ProcessBuilder(command).start();
						} catch (Exception e3) {
							System.out.println("Exception e3, jar not found");
						}
				} else {
					System.out.println("App was not found...");
				}
			}
		});
		JMenuItem defaultItem = new JMenuItem("Exit");
		defaultItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setOpacity(1);
			}
		});
		popupMenu.add(defaultItem);

		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setOpacity(1);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setOpacity((float) 0.5);
			}
		});
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setOpacity(1);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setOpacity((float) 0.5);
				list.setVisible(false);
				render.setPreferredSize(new Dimension(WIDTH, HEIGHT));
				frame.pack();
			}
		});
		textField.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				list.setVisible(true);
				render.setPreferredSize(new Dimension(WIDTH, HEIGHT + list.getPreferredSize().height));
				frame.pack();
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}

		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				textField.setText(list.getSelectedValue());
			}
		});

		render.add(textField);
		render.setOpaque(false);
		frame.setBackground(new Color(0, 0, 0, 0));
		list.setBackground(Color.LIGHT_GRAY);
		list.setVisible(false);
		render.add(list);
		frame.validate();
	}

	private void setupWindow() {
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("My AppExplorer");
		frame.setType(Type.UTILITY);
		frame.setUndecorated(true);
		frame.setOpacity((float) 0.5);
		frame.add(render = new Render(WIDTH, HEIGHT));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		// frame.setIconImage(icon);
		frame.setLocation(screen.width - WIDTH - 20, 20);
		frame.setResizable(false);
		frame.setAlwaysOnTop(false);
		frame.setAutoRequestFocus(false);
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mp) {
				posX = mp.getPoint().x;
				posY = mp.getPoint().y;
			}
		});
		frame.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent md) {
				frame.setLocation(md.getLocationOnScreen().x - posX, md.getLocationOnScreen().y - posY);
			}
		});
		frame.validate();
		frame.pack();
		frame.toBack();
		frame.setVisible(true);
	}

	private void setWindowAdapters() {
		frame.addWindowFocusListener(new WindowAdapter() {

			@Override
			public void windowGainedFocus(WindowEvent e) {
				frame.setOpacity(1);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				frame.setOpacity((float) 0.5);
			}
		});
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			};
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);

			trayIcon = new TrayIcon(icon, "AppExplorer", popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent mc) {
					if (SwingUtilities.isLeftMouseButton(mc) && mc.getClickCount() > 1) {
						if (frame.getExtendedState() == JFrame.ICONIFIED) {
							frame.setVisible(true);
							frame.setExtendedState(JFrame.NORMAL);
						} else {
							frame.setVisible(false);
							frame.setExtendedState(JFrame.ICONIFIED);
						}
					}
				}
			});
			try {
				tray.add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new AppExplorer();
	}
}
