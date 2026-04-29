package gestao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class nodos extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private JTextField txtNomeNodo_n, txtPortas_n, txtIpGestao_n;
    private JComboBox<String> cbTipo_n, cbPai_n, cbCliente_n, cbCliente_e, cbNodo_e;
    

    public nodos(Connection conn) {
        setTitle("Gestão de Infraestrutura (Nodos)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 549, 511);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // --- ABA NOVO NODO ---
        JPanel tabNovo = new JPanel(null);
        tabbedPane.addTab("Novo Nodo", tabNovo);
        montarAbaNovo(tabNovo);
        
        JPanel tabEliminar = new JPanel(null);
        tabbedPane.addTab("Eliminar Nodo", tabEliminar);
        montarAbaEliminar(tabEliminar);
        
        preencherCombos();
    }

    private void montarAbaNovo(JPanel panel) {
        JLabel lblNome = new JLabel("Nome do Nodo:");
        lblNome.setBounds(50, 40, 120, 20);
        panel.add(lblNome);

        txtNomeNodo_n = new JTextField();
        txtNomeNodo_n.setBounds(180, 40, 250, 30);
        panel.add(txtNomeNodo_n);

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setBounds(50, 90, 120, 20);
        panel.add(lblTipo);

        cbTipo_n = new JComboBox<>(new String[] {"Router", "Switch", "AP"});
        cbTipo_n.setBounds(180, 90, 250, 30);
        panel.add(cbTipo_n);

        JLabel lblPortas = new JLabel("Nº de Portas:");
        lblPortas.setBounds(50, 140, 120, 20);
        panel.add(lblPortas);

        txtPortas_n = new JTextField();
        txtPortas_n.setBounds(180, 140, 100, 30);
        panel.add(txtPortas_n);

        JLabel lblIp = new JLabel("IP de Gestão:");
        lblIp.setBounds(50, 190, 120, 20);
        panel.add(lblIp);

        txtIpGestao_n = new JTextField();
        txtIpGestao_n.setBounds(180, 190, 250, 30);
        panel.add(txtIpGestao_n);

        JLabel lblPai = new JLabel("Liga ao Nodo:");
        lblPai.setBounds(50, 272, 120, 20);
        panel.add(lblPai);

        cbPai_n = new JComboBox<>();
        cbPai_n.setBounds(180, 272, 250, 30);
        panel.add(cbPai_n);

        JLabel lblCli = new JLabel("Cliente Proprietário:");
        lblCli.setBounds(50, 231, 120, 20);
        panel.add(lblCli);

        cbCliente_n = new JComboBox<>();
        cbCliente_n.setBounds(180, 231, 250, 30);
        panel.add(cbCliente_n);
        
        cbCliente_n.addActionListener(e -> {
            int idCli = extrairIdDaCombo(cbCliente_n);
            if (idCli > 0) {
                preencherPaiPorCliente(idCli);
            } else {
                // Se não houver cliente, o "Pai" volta a ser apenas o Router Principal
                cbPai_n.removeAllItems();
                cbPai_n.addItem("0 - Nenhum (Router Principal)");
            }
        });

        JButton btnGravar = new JButton("Criar Nodo");
        btnGravar.setBounds(180, 360, 150, 35);
        btnGravar.addActionListener(e -> acaoCriarNodo());
        panel.add(btnGravar);
    }
    
    private void montarAbaEliminar(JPanel panel) {
        JLabel lblCli = new JLabel("1. Selecione o Cliente:");
        lblCli.setBounds(50, 40, 150, 20);
        panel.add(lblCli);

        cbCliente_e = new JComboBox<>();
        cbCliente_e.setBounds(200, 40, 250, 30);
        panel.add(cbCliente_e);

        JLabel lblNodo = new JLabel("2. Selecione o Nodo:");
        lblNodo.setBounds(50, 100, 150, 20);
        panel.add(lblNodo);

        cbNodo_e = new JComboBox<>();
        cbNodo_e.setBounds(200, 100, 250, 30);
        panel.add(cbNodo_e);

        JButton btnEliminar = new JButton("Remover Nodo");
        btnEliminar.setBackground(new Color(204, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setBounds(200, 180, 180, 40);
        panel.add(btnEliminar);

        // --- EVENTOS ---
        cbCliente_e.addActionListener(e -> {
            int idCli = extrairIdDaCombo(cbCliente_e);
            if (idCli > 0) preencherNodosEliminar(idCli);
        });

        btnEliminar.addActionListener(e -> acaoEliminarNodo());
    }

    private void acaoCriarNodo() {
        try {
            String nome = txtNomeNodo_n.getText().trim();
            String tipo = cbTipo_n.getSelectedItem().toString();
            int portas = Integer.parseInt(txtPortas_n.getText().trim());
            String ip = txtIpGestao_n.getText().trim();
            
            int idPai = Integer.parseInt(cbPai_n.getSelectedItem().toString().split(" - ")[0]);
            int idCli = Integer.parseInt(cbCliente_n.getSelectedItem().toString().split(" - ")[0]);

            jbd db = new jbd();
            if (db.inserirNodo(nome, tipo, portas, idPai, ip, idCli)) {
                JOptionPane.showMessageDialog(this, "Nodo de rede configurado!");
                preencherCombos(); 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: Verifique se preencheu todos os campos corretamente.");
        }
    }
    
    private void preencherPaiPorCliente(int idCliente) {
        cbPai_n.removeAllItems();
        cbPai_n.addItem("0 - Nenhum (Router Principal)"); 
        
        jbd db = new jbd();
        try {
            ResultSet rs = db.getNodosPorCliente(idCliente);
            while (rs != null && rs.next()) {
                cbPai_n.addItem(rs.getInt("id_nodo") + " - " + rs.getString("nome_nodo"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    private int extrairIdDaCombo(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return -1;
        try {
            String texto = combo.getSelectedItem().toString();
            return Integer.parseInt(texto.split(" - ")[0]);
        } catch (Exception e) {
            return -1;
        }
    }
    
    private void preencherNodosEliminar(int idCliente) {
        cbNodo_e.removeAllItems();
        jbd db = new jbd();
        try {
            ResultSet rs = db.getNodosPorCliente(idCliente);
            while (rs != null && rs.next()) {
                cbNodo_e.addItem(rs.getInt("id_nodo") + " - " + rs.getString("nome_nodo"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void acaoEliminarNodo() {
        int idNodo = extrairIdDaCombo(cbNodo_e);
        if (idNodo <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um nodo válido.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this, 
            "Tem a certeza? Se este nodo tiver switches ou equipamentos ligados, a remoção será bloqueada.",
            "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            jbd db = new jbd();
            if (db.eliminarNodo(idNodo)) {
                JOptionPane.showMessageDialog(this, "Nodo removido com sucesso!");
                preencherCombos();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro: Não pode apagar este nodo porque existem equipamentos ou outros nodos ligados a ele.", 
                    "Erro de Integridade", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void preencherCombos() {
        jbd db = new jbd();
        try {
            ResultSet rs = db.getTodosClientes();
            cbCliente_n.removeAllItems();
            cbCliente_e.removeAllItems();            
            cbCliente_n.addItem("0 - Selecione o Cliente...");
            cbCliente_e.addItem("0 - Selecione o Cliente...");
            while (rs != null && rs.next()) {
                String item = rs.getInt("id_cliente") + " - " + rs.getString("nome_cliente");
                cbCliente_n.addItem(item);
                cbCliente_e.addItem(item);
                }
            cbPai_n.removeAllItems();
            cbPai_n.addItem("0 - Nenhum (Router Principal)");
            cbNodo_e.removeAllItems();
            
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
}