package gestao;


import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;


import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.awt.Color;
import java.awt.Label;
import java.awt.TextField;
import javax.swing.JTextPane;

public class main {
	
	private static boolean tunelIniciado = false;
	private JFrame frame;
	private JTextField textuser;
	private JPasswordField txtpass;
	private java.sql.Connection conn = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main window = new main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	public class Sessao {
	    public static String username = "";
	    public static String nomeNivel = "";
	    public static int idUser = 0;
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 671, 434);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JPanel Entrada = new JPanel();
		Entrada.setBackground(Color.DARK_GRAY);
		frame.getContentPane().add(Entrada, "name_14926939898600");
		Entrada.setLayout(null);
		
		JPanel Menu = new JPanel();
		Menu.setBackground(Color.GRAY);
		frame.getContentPane().add(Menu, "name_14896799922500");
		Menu.setLayout(null);
		
		JLabel lblLabel = new JLabel("Utilizador:");
		lblLabel.setBounds(10, 11, 77, 14);
		Menu.add(lblLabel);
		
		JLabel lblnameuser = new JLabel("New label");
		lblnameuser.setBounds(97, 11, 110, 14);
		Menu.add(lblnameuser);
		
		JLabel lblacesso = new JLabel("Nivel de acesso:");
		lblacesso.setBounds(425, 11, 120, 14);
		Menu.add(lblacesso);
		
		JLabel lblniveluser = new JLabel("New label");
		lblniveluser.setBounds(549, 11, 89, 14);
		Menu.add(lblniveluser);
		
		JButton btngerirUtilizador = new JButton("Gerir Utilizador");
		btngerirUtilizador.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("1. Clique detetado"); // Ver no console
		        
		        int idselecionado = 1; 
		        
		        if (conn == null) {
		            System.out.println("ERRO: A ligação 'conn' está nula!");
		            JOptionPane.showMessageDialog(null, "Erro: Sem ligação à base de dados.");
		            return;
		        }

		        utilizador janAdmin = new utilizador(idselecionado, true, conn);
		        System.out.println("2. Janela criada com sucesso");
		        
		        janAdmin.setLocationRelativeTo(null); // Centraliza no ecrã
		        janAdmin.setVisible(true);
		        System.out.println("3. setVisible chamado");
			}
		});
		btngerirUtilizador.setBounds(97, 69, 135, 64);
		Menu.add(btngerirUtilizador);
		
		JButton btnConfiguracoes = new JButton("Configurações");
		btnConfiguracoes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("A abrir configurações para o ID: " + Sessao.idUser);
				utilizador janConfig = new utilizador (Sessao.idUser, false, conn);
		        janConfig.setVisible(true);

			}
		});
		btnConfiguracoes.setBounds(254, 69, 135, 64);
		Menu.add(btnConfiguracoes);
		
		JButton btnGerirClientes = new JButton("Gerir clientes");
		btnGerirClientes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				clientes janConfig = new clientes(conn);
		        janConfig.setVisible(true);
		        janConfig.setLocationRelativeTo(null);
				
			}
		});
		btnGerirClientes.setBounds(425, 69, 135, 64);
		Menu.add(btnGerirClientes);
		
		JButton btnAdicionarEquipamento = new JButton("<html><center>Gerir<br>Equipamento</center></html>");
		btnAdicionarEquipamento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				equipamento janConfig  = new equipamento(conn);
				janConfig.setVisible(true);
		        janConfig.setLocationRelativeTo(null);

			}
		});
		btnAdicionarEquipamento.setBounds(97, 144, 135, 64);
		Menu.add(btnAdicionarEquipamento);
		
		JButton btnGerirNodos = new JButton("Gerir Nodos");
		btnGerirNodos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nodos jan = new nodos(conn);
			    jan.setVisible(true);
			    jan.setLocationRelativeTo(null);
				
			}
		});
		btnGerirNodos.setBounds(425, 144, 135, 64);
		Menu.add(btnGerirNodos);
		
		JButton btnInventrioGeral = new JButton("Inventário Geral");
		btnInventrioGeral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventario jan = new inventario(conn);
			    jan.setVisible(true);
			    jan.setLocationRelativeTo(null);
			}
		});
		btnInventrioGeral.setBounds(97, 219, 135, 64);
		Menu.add(btnInventrioGeral);
		
		JButton btnProcurarMac = new JButton("Procurar MAC");
		btnProcurarMac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pesquisar jan = new pesquisar(conn);
			    jan.setVisible(true);
			    jan.setLocationRelativeTo(null);
			}
		});
		btnProcurarMac.setBounds(254, 219, 135, 64);
		Menu.add(btnProcurarMac);
		
		JButton btnRelatrios = new JButton("Relatórios");
		btnRelatrios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				relatorios jan = new relatorios(conn);
			    jan.setVisible(true);
			    jan.setLocationRelativeTo(null);
			}
		});
		btnRelatrios.setBounds(425, 219, 135, 64);
		Menu.add(btnRelatrios);
		
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});
		btnSair.setBounds(168, 294, 135, 64);
		Menu.add(btnSair);
		
		JButton btnVoltarAoLogin = new JButton("Voltar ao login");
		btnVoltarAoLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
			        textuser.setText("");
			        txtpass.setText("");
			        Sessao.username = "";
			        Sessao.nomeNivel = "";
			        if (conn != null && !conn.isClosed()) {
			            conn.close();
			        }
			        Menu.setVisible(false);
			        Entrada.setVisible(true);
			        System.out.println("Logout efetuado.");
			    } catch (SQLException ex) {
			    	System.out.println("Erro ao fazer logout: " + ex.getMessage());
	
			    }
			}
		});
		btnVoltarAoLogin.setBounds(348, 294, 135, 64);
		Menu.add(btnVoltarAoLogin);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\jorge\\testes\\ATEC\\UC00606 - Desenvolver programas em linguagem estruturada\\Torre\\Projeto_gestor_de_redes\\src\\IMG\\redes.png"));
		lblNewLabel.setBounds(0, 0, 655, 395);
		Menu.add(lblNewLabel);
		
		
		JLabel lblTitulo = new JLabel("Bem-vindo ao Inventário de Redes", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
		lblTitulo.setBounds(158, 35, 344, 19);
		Entrada.add(lblTitulo);
		
		JLabel lbluser = new JLabel("Utilizador:");
		lbluser.setBounds(67, 123, 95, 30);
		Entrada.add(lbluser);
		
		JLabel lblpass = new JLabel("Palavra passe:");
		lblpass.setBounds(67, 213, 95, 30);
		Entrada.add(lblpass);
		
		textuser = new JTextField();
		textuser.setBounds(211, 123, 161, 30);
		Entrada.add(textuser);
		textuser.setColumns(10);
		
		txtpass = new JPasswordField();
		txtpass.setBounds(211, 213, 161, 30);
		Entrada.add(txtpass);
		
		JButton btnsair = new JButton("Sair");
		btnsair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); 
			}
		});
		btnsair.setBounds(142, 299, 128, 51);
		Entrada.add(btnsair);
		
		
		JButton btnentrar = new JButton("Entrar");
		frame.getRootPane().setDefaultButton(btnentrar);
		btnentrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				String utilizador = textuser.getText().trim();
		        String palavraPasse = new String(txtpass.getPassword()).trim();

		        try {
		            if (utilizador.isEmpty() || palavraPasse.isEmpty()) {
		                JOptionPane.showMessageDialog(null, "Preencha tudo!", "Aviso", JOptionPane.WARNING_MESSAGE);
		                return; 
		            }

		            jbd baseDados = new jbd();

		            
		            if (!tunelIniciado) {
		                baseDados.startTunnel();
		                tunelIniciado = true;
		                try { Thread.sleep(2000); } catch (Exception ex) {}
		            }

		            conn = (java.sql.Connection) baseDados.ligar();

		            if (conn != null) {

		                String sql = "SELECT u.id_user, u.username, n.nome_nivel " +
		                             "FROM utilizadores u " +
		                             "INNER JOIN niveis_acesso n ON u.id_nivel = n.id_nivel " +
		                             "WHERE u.username = ? AND u.password = ?";

		                java.sql.PreparedStatement stmt = ((java.sql.Connection) conn).prepareStatement(sql);
		                stmt.setString(1, utilizador);
		                stmt.setString(2, palavraPasse);

		                java.sql.ResultSet rs = stmt.executeQuery();
		                if (rs.next()) {
		                	Sessao.idUser = rs.getInt("id_user"); 
		                    Sessao.username = rs.getString("username");
		                	String nivelAcesso = rs.getString("nome_nivel");
		                	lblnameuser.setText(utilizador);
		                	lblniveluser.setText(nivelAcesso);
		                	btngerirUtilizador.setVisible(false);
		                    btnGerirClientes.setVisible(false);
		                    btnAdicionarEquipamento.setVisible(false);
		                    btnGerirNodos.setVisible(false);
		                    btnInventrioGeral.setVisible(false);
		                    btnProcurarMac.setVisible(false);
		                    btnRelatrios.setVisible(false);
		                    if ("Administrador".equalsIgnoreCase(nivelAcesso)) {
		                        btngerirUtilizador.setVisible(true);
		                        btnConfiguracoes.setVisible(true);
		                        btnGerirClientes.setVisible(true);
		                        btnAdicionarEquipamento.setVisible(true);
		                        btnGerirNodos.setVisible(true);
		                        btnInventrioGeral.setVisible(true);
		                        btnProcurarMac.setVisible(true);
		                        btnRelatrios.setVisible(true);
		                    
		                    } else if ("Colaborador".equalsIgnoreCase(nivelAcesso)) {
		                    	btnConfiguracoes.setVisible(true);
		                        btnAdicionarEquipamento.setVisible(true);
		                        btnGerirNodos.setVisible(true);
		                        btnInventrioGeral.setVisible(true);
		                        btnProcurarMac.setVisible(true);
		                        btnRelatrios.setVisible(true);

		                    } else if ("Cliente".equalsIgnoreCase(nivelAcesso)) {
		                    	btnConfiguracoes.setVisible(true);
		                        btnInventrioGeral.setVisible(true);
		                        btnProcurarMac.setVisible(true);
		                        btnRelatrios.setVisible(true);

		                    } else {
		                        System.out.println("Acesso limitado para convidado.");
		                        btnConfiguracoes.setVisible(true);
		                        btnProcurarMac.setVisible(true);
		                    }
		                    

		                    JOptionPane.showMessageDialog(null, 
		                        "Bem-vindo(a), " + utilizador + "\nNível: " + nivelAcesso, 
		                        "Sucesso", 
		                        JOptionPane.INFORMATION_MESSAGE);
		                    
		                    Entrada.setVisible(false); 
		                    Menu.setVisible(true); 
		                    
		                    Menu.revalidate();
		                    Menu.repaint();  

		                } else {
		                    JOptionPane.showMessageDialog(null, "Credenciais inválidas!", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
		                }

		                rs.close();
		                stmt.close();

		            } else {
		                JOptionPane.showMessageDialog(null, "Falha ao ligar à Base de Dados através do túnel.", "Erro de Ligação", JOptionPane.ERROR_MESSAGE);
		            }

		        } catch (Exception ex) {
		            JOptionPane.showMessageDialog(null, 
		                "Ocorreu um erro no sistema: " + ex.getMessage(), 
		                "Erro Crítico", 
		                JOptionPane.ERROR_MESSAGE);
		        }			
		    }
			
		});
		btnentrar.setBounds(359, 299, 128, 51);
		Entrada.add(btnentrar);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\jorge\\testes\\ATEC\\UC00606 - Desenvolver programas em linguagem estruturada\\Torre\\Projeto_gestor_de_redes\\src\\IMG\\2890713.png"));
		lblNewLabel_1.setBounds(433, -40, 263, 808);
		Entrada.add(lblNewLabel_1);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, "name_13315626310900");
	}
}
