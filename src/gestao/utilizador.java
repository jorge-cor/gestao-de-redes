package gestao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class utilizador extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int idAlvo;
	private boolean adminMode;
	private Connection conn;
	private JTextField txtUsername_c;
	private JPasswordField txtPassword_c;
	private JTextField txtBusca_a;
	private JComboBox<String> cbNivel_c;
	private JComboBox<String> cbCliente_c;
	private JTextField txtUsername_n;
	private JPasswordField txtPassword_n;
	private JComboBox<String> cbNivel_n;
	private JComboBox<String> cbCliente_n;
	private JComboBox<String> cbApagarUser;

	public utilizador(int idParaEditar, boolean modoAdmin, Connection conexaoAtiva) {
		this.idAlvo = idParaEditar;
		this.adminMode = modoAdmin;
		this.conn = conexaoAtiva;

		setTitle(modoAdmin ? "Gestão de Utilizadores" : "As Minhas Configurações");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel tabConfigurar = new JPanel();
		tabConfigurar.setLayout(null);
		tabbedPane.addTab("Configurar Utilizador", null, tabConfigurar, null);
		montarAbaConfigurar(tabConfigurar);

		JPanel tabNovo = new JPanel();
		tabNovo.setLayout(null);
		if (adminMode) {
			tabbedPane.addTab("Novo Utilizador", null, tabNovo, null);
			montarAbaNovo(tabNovo);
		}

		JPanel tabApagar = new JPanel();
		tabApagar.setLayout(null);
		if (adminMode) {
			tabbedPane.addTab("Apagar Utilizador", null, tabApagar, null);
			montarAbaApagar(tabApagar);
		}
		
		 // Ação de Preencher comboBox
		preencherCombos();
		
		 // Ação de Carregar dados de Utilizador
		carregarDadosUtilizador();
	}

	private void montarAbaConfigurar(JPanel panel) {
		JLabel lblUser = new JLabel("Username:");
		lblUser.setBounds(50, 40, 120, 20);
		panel.add(lblUser);

		txtUsername_c = new JTextField();
		txtUsername_c.setBounds(180, 40, 250, 30);
		panel.add(txtUsername_c);
		
		// Lógica de bloqueio de utilizador
		if (!adminMode) {
			txtUsername_c.setText(Sessao.username); 
		    txtUsername_c.setEditable(false);
		    txtUsername_c.setEnabled(false);
	    }

		JLabel lblPass = new JLabel("Nova Password:");
		lblPass.setBounds(50, 90, 120, 20);
		panel.add(lblPass);

		txtPassword_c = new JPasswordField();
		txtPassword_c.setBounds(180, 90, 250, 30);
		panel.add(txtPassword_c);

		JLabel lblNivel = new JLabel("Nível de Acesso:");
		lblNivel.setBounds(50, 140, 120, 20);
		panel.add(lblNivel);

		cbNivel_c = new JComboBox<>();
		cbNivel_c.setBounds(180, 140, 250, 30);
		panel.add(cbNivel_c);
		if (!adminMode) cbNivel_c.setEnabled(false);

		JLabel lblCliente = new JLabel("Cliente Associado:");
		lblCliente.setBounds(50, 190, 120, 20);
		panel.add(lblCliente);

		cbCliente_c = new JComboBox<>();
		cbCliente_c.setBounds(180, 190, 250, 30);
		panel.add(cbCliente_c);
		if (!adminMode) cbCliente_c.setEnabled(false);

		JButton btnGravar = new JButton("Gravar Alterações");
		btnGravar.setBounds(180, 350, 150, 35);
		btnGravar.addActionListener(e -> acaoAtualizar());
		panel.add(btnGravar);

		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.setBounds(340, 350, 100, 35);
		btnCancelar.addActionListener(e -> dispose());
		panel.add(btnCancelar);
	}
	private void montarAbaNovo(JPanel panel) {
	    JLabel lblUser = new JLabel("Username:");
	    lblUser.setBounds(50, 40, 120, 20);
	    panel.add(lblUser);

	    txtUsername_n = new JTextField();
	    txtUsername_n.setBounds(180, 40, 250, 30);
	    panel.add(txtUsername_n);

	    JLabel lblPass = new JLabel("Password:");
	    lblPass.setBounds(50, 90, 120, 20);
	    panel.add(lblPass);

	    txtPassword_n = new JPasswordField();
	    txtPassword_n.setBounds(180, 90, 250, 30);
	    panel.add(txtPassword_n);

	    JLabel lblNivel = new JLabel("Nível de Acesso:");
	    lblNivel.setBounds(50, 140, 120, 20);
	    panel.add(lblNivel);

	    cbNivel_n = new JComboBox<>();
	    cbNivel_n.setBounds(180, 140, 250, 30);
	    panel.add(cbNivel_n);

	    JLabel lblCliente = new JLabel("Cliente Associado:");
	    lblCliente.setBounds(50, 190, 120, 20);
	    panel.add(lblCliente);

	    cbCliente_n = new JComboBox<>();
	    cbCliente_n.setBounds(180, 190, 250, 30);
	    panel.add(cbCliente_n);

	    JButton btnCriar = new JButton("Criar Utilizador");
	    btnCriar.setBounds(180, 350, 150, 35);
	    btnCriar.addActionListener(e -> acaoCriarNovoUser());
	    panel.add(btnCriar);

	    JButton btnLimpar = new JButton("Limpar");
	    btnLimpar.setBounds(340, 350, 100, 35);
	    btnLimpar.addActionListener(e -> {
	        txtUsername_n.setText("");
	        txtPassword_n.setText("");
	    });
	    panel.add(btnLimpar);
	}

	private void montarAbaApagar(JPanel panel) {
	    JLabel lblProcurar = new JLabel("Procurar nome:");
	    lblProcurar.setBounds(50, 40, 120, 20);
	    panel.add(lblProcurar);

	    txtBusca_a = new JTextField(); 
	    txtBusca_a.setBounds(180, 40, 180, 30);
	    panel.add(txtBusca_a);

	    JButton btnLupa = new JButton("Procurar");
	    btnLupa.setBounds(370, 40, 100, 30);
	    panel.add(btnLupa);

	    JLabel lblSelecionar = new JLabel("Selecionar:");
	    lblSelecionar.setBounds(50, 100, 120, 20);
	    panel.add(lblSelecionar);

	    cbApagarUser = new JComboBox<String>();
	    cbApagarUser.setBounds(180, 100, 250, 30);
	    panel.add(cbApagarUser);

	    JButton btnEliminar = new JButton("Apagar Utilizador");
	    btnEliminar.setBackground(new Color(255, 102, 102)); // Um tom de vermelho
	    btnEliminar.setBounds(180, 200, 150, 40);
	    panel.add(btnEliminar);


	    // Ação de Procurar
	    btnLupa.addActionListener(e -> {
	        preencherComboApagar(txtBusca_a.getText());
	    });

	    // Ação de Apagar
	    btnEliminar.addActionListener(e -> {
	        acaoEliminar();
	    });
	}
	
	private void preencherComboApagar(String filtro) {
	    cbApagarUser.removeAllItems(); // Limpa a lista anterior
	    jbd db = new jbd();
	    try {
	        ResultSet rs = db.getUtilizadoresFiltro(filtro);
	        while (rs != null && rs.next()) {
	            // Guardamos "ID - Nome" para sabermos quem apagar depois
	            cbApagarUser.addItem(rs.getInt("id_user") + " - " + rs.getString("username"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private void acaoEliminar() {
	    if (cbApagarUser.getSelectedItem() == null) {
	        JOptionPane.showMessageDialog(this, "Selecione um utilizador primeiro.");
	        return;
	    }

	    // Extrair o ID do texto "ID - Nome"
	    String selecionado = cbApagarUser.getSelectedItem().toString();
	    int idParaApagar = Integer.parseInt(selecionado.split(" - ")[0]);
	    String nomeParaApagar = selecionado.split(" - ")[1];

	    // Pedir confirmação (Segurança!)
	    int resposta = JOptionPane.showConfirmDialog(this, 
	        "Tem a certeza que deseja apagar o utilizador " + nomeParaApagar + "?",
	        "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

	    if (resposta == JOptionPane.YES_OPTION) {
	        jbd db = new jbd();
	        if (db.eliminarUtilizador(idParaApagar)) {
	            JOptionPane.showMessageDialog(this, "Utilizador removido!");
	            cbApagarUser.removeAllItems();
	            txtBusca_a.setText("");
	        } else {
	            JOptionPane.showMessageDialog(this, "Erro ao eliminar. O user pode ter dados associados.");
	        }
	    }
	}


	private void carregarDadosUtilizador() {
	    jbd db = new jbd();
	    try {
	        ResultSet rs = db.getUtilizadorPorId(idAlvo);
	        
	        if (rs != null && rs.next()) {
	            String userBD = rs.getString("username");
	            txtUsername_c.setText(userBD);
	            
	            // Se for admin, a Sessao.username também deve ser atualizada para garantir
	            if (!adminMode) {
	                Sessao.username = userBD; 
	            }

	            String nivelBD = rs.getString("nome_nivel");
	            cbNivel_c.setSelectedItem(nivelBD);
	            
	            String clienteBD = rs.getString("nome_cliente");
	            if (clienteBD != null) {
	                cbCliente_c.setSelectedItem(clienteBD);
	            }
	        }
	    } catch (SQLException e) {
	        // Se der erro de SQL, isto vai avisar-te no console
	        System.out.println("Erro ao carregar dados: " + e.getMessage());
	    }
	}

	private void acaoAtualizar() {
	    jbd db = new jbd();
	    String user = txtUsername_c.getText();
	    
	    // Procura o ID real deste username na BD
	    int idReal = db.getIdPeloUsername(user);
	    
	    if (idReal != -1) {
	        // Se encontrou, usamos o idReal para o update
	        String pass = new String(txtPassword_c.getPassword());
	        String nivel = cbNivel_c.getSelectedItem().toString();
	        String cliente = cbCliente_c.getSelectedItem().toString();

	        if (db.atualizarUtilizador(idReal, user, pass, nivel, cliente)) {
	            JOptionPane.showMessageDialog(this, "Dados de " + user + " atualizados!");
	        }
	    } else {
	        JOptionPane.showMessageDialog(this, "Utilizador não encontrado no sistema.");
	    }
	}
	private void acaoCriarNovoUser() {
	    String user = txtUsername_n.getText().trim();
	    String pass = new String(txtPassword_n.getPassword()).trim();
	    String nivel = cbNivel_n.getSelectedItem().toString();

	    if (user.isEmpty() || pass.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
	        return;
	    }

	    jbd db = new jbd();

	    if (db.inserirUtilizador(user, pass, nivel)) {
	        JOptionPane.showMessageDialog(this, "Utilizador criado com sucesso!");

	        txtUsername_n.setText("");
	        txtPassword_n.setText("");
	    } else {
	        JOptionPane.showMessageDialog(this, "Erro ao criar utilizador.", "Erro", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	private void preencherCombos() {
	    jbd db = new jbd();
	    try {
	        // 1. Preencher Níveis
	        ResultSet rsN = db.getNiveis();
	        while (rsN != null && rsN.next()) {
	            String nome = rsN.getString("nome_nivel");
	            if (cbNivel_c != null) cbNivel_c.addItem(nome);
	            if (cbNivel_n != null) cbNivel_n.addItem(nome);
	        }

	        // 2. Preencher Clientes
	        ResultSet rsC = db.getClientes();
	        while (rsC != null && rsC.next()) {
	            String nomeCli = rsC.getString("nome_cliente");
	            if (cbCliente_c != null) cbCliente_c.addItem(nomeCli);
	            if (cbCliente_n != null) cbCliente_n.addItem(nomeCli);
	        }

	        /*
	        ResultSet rsC = db.getClientes();
	        while (rsC != null && rsC.next()) {
	            String cliente = rsC.getString("nome_cliente");
	            if (cbCliente_c != null) cbCliente_c.addItem(cliente);
	            if (cbCliente_n != null) cbCliente_n.addItem(cliente);
	        }
	        */
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    }
	}
}