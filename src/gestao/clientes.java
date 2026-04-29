package gestao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class clientes extends JFrame {

    private JPanel contentPane;
    private Connection conn;

    // Componentes Novo
    private JTextField txtNome_n, txtContacto_n, txtLocalidade_n;

    // Componentes Configurar (Editar)
    private JComboBox<String> cbSelecionar_e;
    private JTextField txtNome_e, txtContacto_e, txtLocalidade_e;
    private int idClienteEditar = -1;
    private int idClienteAtual = -1;

    // Componentes Apagar
    private JTextField txtBusca_a;
    private JComboBox<String> cbApagar_a;

    public clientes(Connection conexaoAtiva) {
        this.conn = conexaoAtiva;

        setTitle("Gestão de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 500);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // --- ABA NOVO ---
        JPanel tabNovo = new JPanel(null);
        tabbedPane.addTab("Novo Cliente", tabNovo);
        montarAbaNovo(tabNovo);

        // --- ABA EDITAR ---
        JPanel tabEditar = new JPanel(null);
        tabbedPane.addTab("Configurar Cliente", tabEditar);
        montarAbaEditar(tabEditar);

        // --- ABA APAGAR ---
        JPanel tabApagar = new JPanel(null);
        tabbedPane.addTab("Apagar Cliente", tabApagar);
        montarAbaApagar(tabApagar);
        preencherComboClientes();
    }

    private void montarAbaNovo(JPanel panel) {
        JLabel lblNome = new JLabel("Nome Cliente:");
        lblNome.setBounds(50, 40, 120, 20);
        panel.add(lblNome);

        txtNome_n = new JTextField();
        txtNome_n.setBounds(180, 40, 250, 30);
        panel.add(txtNome_n);

        JLabel lblContacto = new JLabel("Contacto:");
        lblContacto.setBounds(50, 90, 120, 20);
        panel.add(lblContacto);

        txtContacto_n = new JTextField();
        txtContacto_n.setBounds(180, 90, 250, 30);
        panel.add(txtContacto_n);

        JLabel lblLocal = new JLabel("Localidade:");
        lblLocal.setBounds(50, 140, 120, 20);
        panel.add(lblLocal);

        txtLocalidade_n = new JTextField();
        txtLocalidade_n.setBounds(180, 140, 250, 30);
        panel.add(txtLocalidade_n);

        JButton btnGravar = new JButton("Criar Cliente");
        btnGravar.setBounds(180, 350, 150, 35);
        btnGravar.addActionListener(e -> acaoCriarCliente());
        panel.add(btnGravar);
    }

    private void montarAbaEditar(JPanel panel) {
        JLabel lblSel = new JLabel("Selecionar Cliente:");
        lblSel.setBounds(50, 40, 120, 20);
        panel.add(lblSel);

        cbSelecionar_e = new JComboBox<String>();
        cbSelecionar_e.setBounds(180, 40, 250, 30);
        panel.add(cbSelecionar_e);

        JButton btnCarregar = new JButton("Carregar");
        btnCarregar.setBounds(440, 40, 100, 30);
        panel.add(btnCarregar);

        // Linha divisória
        JSeparator separator = new JSeparator();
        separator.setBounds(50, 90, 490, 2);
        panel.add(separator);

        // Campos de edição
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(50, 120, 120, 20);
        panel.add(lblNome);

        txtNome_e = new JTextField();
        txtNome_e.setBounds(180, 120, 250, 30);
        panel.add(txtNome_e);

        JLabel lblCont = new JLabel("Contacto:");
        lblCont.setBounds(50, 170, 120, 20);
        panel.add(lblCont);

        txtContacto_e = new JTextField();
        txtContacto_e.setBounds(180, 170, 250, 30);
        panel.add(txtContacto_e);

        JLabel lblLoc = new JLabel("Localidade:");
        lblLoc.setBounds(50, 220, 120, 20);
        panel.add(lblLoc);

        txtLocalidade_e = new JTextField();
        txtLocalidade_e.setBounds(180, 220, 250, 30);
        panel.add(txtLocalidade_e);

        JButton btnGravar = new JButton("Gravar Alterações");
        btnGravar.setBounds(180, 350, 150, 35);
        panel.add(btnGravar);

        // --- EVENTOS ---
        btnCarregar.addActionListener(e -> acaoCarregarDados());
        btnGravar.addActionListener(e -> acaoAtualizarCliente());
    }

    private void montarAbaApagar(JPanel panel) {
        JLabel lblBusca = new JLabel("Procurar Cliente:");
        lblBusca.setBounds(50, 40, 120, 20);
        panel.add(lblBusca);

        txtBusca_a = new JTextField();
        txtBusca_a.setBounds(180, 40, 180, 30);
        panel.add(txtBusca_a);

        JButton btnLupa = new JButton("Procurar");
        btnLupa.setBounds(370, 40, 100, 30);
        panel.add(btnLupa);

        JLabel lblSel = new JLabel("Selecionar para Apagar:");
        lblSel.setBounds(50, 100, 150, 20);
        panel.add(lblSel);

        cbApagar_a = new JComboBox<String>();
        cbApagar_a.setBounds(180, 100, 250, 30);
        panel.add(cbApagar_a);

        JButton btnEliminar = new JButton("Remover Cliente");
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(204, 0, 0)); // Vermelho alerta
        btnEliminar.setBounds(180, 200, 160, 40);
        panel.add(btnEliminar);

        // --- EVENTOS ---
        btnLupa.addActionListener(e -> preencherComboApagar(txtBusca_a.getText()));
        btnEliminar.addActionListener(e -> acaoEliminarCliente());

    }
    
    private void acaoCriarCliente() {
        // 1. Recolher os dados dos campos de texto da aba "Novo"
        String nome = txtNome_n.getText().trim();
        String contacto = txtContacto_n.getText().trim();
        String localidade = txtLocalidade_n.getText().trim();

        // 2. Validação simples: não deixar criar clientes sem nome
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do cliente é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Chamar o motor da Base de Dados
        jbd db = new jbd();
        
        if (db.inserirCliente(nome, contacto, localidade)) {
            // Sucesso!
            JOptionPane.showMessageDialog(this, "Cliente '" + nome + "' criado com sucesso!");
            
            // Limpar os campos para a próxima inserção
            txtNome_n.setText("");
            txtContacto_n.setText("");
            txtLocalidade_n.setText("");
            
            // atualizarCombos(); 
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao criar cliente. Verifique se o nome já existe.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherComboClientes() {
        cbSelecionar_e.removeAllItems();
        jbd db = new jbd();
        try {
            ResultSet rs = db.getTodosClientes();
            while (rs != null && rs.next()) {
                // Guardamos "ID - Nome" para facilitar a busca
                cbSelecionar_e.addItem(rs.getInt("id_cliente") + " - " + rs.getString("nome_cliente"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void acaoCarregarDados() {
        if (cbSelecionar_e.getSelectedItem() == null) return;
        
        String item = cbSelecionar_e.getSelectedItem().toString();
        this.idClienteAtual = Integer.parseInt(item.split(" - ")[0]);
        System.out.println("A carregar cliente ID: " + idClienteAtual);

        jbd db = new jbd();
        try {
            // Podes usar um SELECT * FROM clientes WHERE id_cliente = idClienteAtual
            Connection c = db.ligar();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM clientes WHERE id_cliente = ?");
            ps.setInt(1, idClienteAtual);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtNome_e.setText(rs.getString("nome_cliente"));
                txtContacto_e.setText(rs.getString("contacto"));
                txtLocalidade_e.setText(rs.getString("localidade"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Grava as mudanças
    private void acaoAtualizarCliente() {
        int idClienteAtual = 0;
		if (idClienteAtual == -1) return;
		String novoNome = txtNome_e.getText().trim();
		String novoCont = txtContacto_e.getText().trim();
		String novaLoc = txtLocalidade_e.getText().trim();

        jbd db = new jbd();
        if (db.atualizarCliente(this.idClienteAtual, novoNome, novoCont, novaLoc)) {
            JOptionPane.showMessageDialog(this, "Sucesso!");
        } else {
            // Se chegar aqui sem erro no terminal, é porque o MySQL devolveu 0 rows affected
            JOptionPane.showMessageDialog(this, "Nada foi alterado ou cliente não encontrado.");
        }
    }
    private void preencherComboApagar(String filtro) {
        cbApagar_a.removeAllItems();
        jbd db = new jbd();
        try {
            ResultSet rs = db.getClientesFiltro(filtro);
            while (rs != null && rs.next()) {
                cbApagar_a.addItem(rs.getInt("id_cliente") + " - " + rs.getString("nome_cliente"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void acaoEliminarCliente() {
        if (cbApagar_a.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente primeiro.");
            return;
        }

        String selecionado = cbApagar_a.getSelectedItem().toString();
        int idParaApagar = Integer.parseInt(selecionado.split(" - ")[0]);
        String nomeParaApagar = selecionado.split(" - ")[1];

        // Pergunta de segurança (Fundamental para não apagar o que não deve!)
        int confirmar = JOptionPane.showConfirmDialog(this, 
            "Atenção! Deseja mesmo apagar o cliente: " + nomeParaApagar + "?\nIsto pode afetar utilizadores associados.",
            "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            jbd db = new jbd();
            if (db.eliminarCliente(idParaApagar)) {
                JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!");
                txtBusca_a.setText("");
                cbApagar_a.removeAllItems();
                // Atualiza também a combo da aba de Edição para manter tudo em sincronia
                preencherComboClientes(); 
            } else {
                JOptionPane.showMessageDialog(this, "Erro: Não é possível apagar este cliente porque ele tem utilizadores associados.", "Erro de Integridade", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}