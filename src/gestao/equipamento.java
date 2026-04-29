package gestao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Connection; 
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.text.MaskFormatter;
import javax.swing.*;

public class equipamento extends JFrame {
    private JPanel contentPane;
    private JTextField txtNomeEquip_n, txtSala_n;
    private JComboBox<String> cbNodo_n, cbCliente_n;
    private JComboBox<String> cbCliente_e, cbEquipamento_e;

    private JFormattedTextField txtMac_n;
    
    public equipamento(Connection conn) {
        setTitle("Gestão de Equipamentos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 550, 500); 
        
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0)); 
        setContentPane(contentPane);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // --- ABA ADICIONAR ---
        JPanel tabNovo = new JPanel(null); 
        tabbedPane.addTab("Adicionar Novo", tabNovo);
        montarAbaNovo(tabNovo);

        // --- ABA ELIMINAR ---
        JPanel tabEliminar = new JPanel(null);
        tabbedPane.addTab("Eliminar Equipamento", tabEliminar);
        montarAbaEliminar(tabEliminar);

        // Carregar dados iniciais
        preencherCombos();
    }

    private void montarAbaNovo(JPanel panel) {
        JLabel lblNome = new JLabel("Nome Dispositivo:");
        lblNome.setBounds(50, 90, 120, 20);
        panel.add(lblNome);

        txtNomeEquip_n = new JTextField();
        txtNomeEquip_n.setBounds(180, 85, 250, 30);
        panel.add(txtNomeEquip_n);

        JLabel lblMac = new JLabel("MAC Address:");
        lblMac.setBounds(50, 135, 120, 20);
        panel.add(lblMac);       
        
        JLabel lblSala = new JLabel("Sala / Localização:");
        lblSala.setBounds(50, 49, 120, 20); // y=140
        panel.add(lblSala);

        txtSala_n = new JTextField();
        txtSala_n.setBounds(180, 44, 250, 30);
        panel.add(txtSala_n);

        try {
            // A máscara "HH:HH:HH:HH:HH:HH" aceita apenas caracteres Hexadecimais (0-9, A-F)
            MaskFormatter mascaraMac = new MaskFormatter("HH:HH:HH:HH:HH:HH");
            mascaraMac.setPlaceholderCharacter('_'); // Mostra onde faltam caracteres
            
            txtMac_n = new JFormattedTextField(mascaraMac);
        } catch (Exception e) {
            txtMac_n = new JFormattedTextField();
        }
        txtMac_n.setBounds(180, 135, 250, 30);
        panel.add(txtMac_n);

        JLabel lblNodo = new JLabel("Nodo:");
        lblNodo.setBounds(50, 225, 120, 20);
        panel.add(lblNodo);

        cbNodo_n = new JComboBox<>();
        cbNodo_n.setBounds(180, 220, 250, 30);
        panel.add(cbNodo_n);

        JLabel lblCli = new JLabel("Cliente:");
        lblCli.setBounds(50, 181, 120, 20);
        panel.add(lblCli);

        cbCliente_n = new JComboBox<>();
        cbCliente_n.setBounds(180, 176, 250, 30);
        panel.add(cbCliente_n);
        
        cbCliente_n.addActionListener(e -> {
            int idCli = extrairIdDaCombo(cbCliente_n);
            if (idCli > 0) {
                preencherComboNodosFiltrados(idCli);
            }
        });

        JButton btnGravar = new JButton("Adicionar Equipamento");
        btnGravar.setBounds(180, 300, 180, 35);
        btnGravar.addActionListener(e -> acaoCriarEquipamento());
        panel.add(btnGravar);
    }
    
    private void acaoCriarEquipamento() {
        String nome = txtNomeEquip_n.getText().trim();
        String mac = txtMac_n.getText().toUpperCase().trim();
        String sala = txtSala_n.getText().trim(); 

        if (nome.isEmpty() || mac.isEmpty() || sala.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, MAC e Sala são obrigatórios!");
            return;
        }

        int idNodo = extrairIdDaCombo(cbNodo_n);
        int idCliente = extrairIdDaCombo(cbCliente_n);

        jbd db = new jbd();
        
        // Verificação de portas
        if (db.getPortasOcupadas(idNodo) >= db.getTotalPortas(idNodo)) {
            JOptionPane.showMessageDialog(this, "Nodo sem portas livres!");
            return;
        }

        // Chama o inserir com o novo parâmetro 'sala'
        if (db.inserirEquipamento(nome, mac, sala, idNodo, idCliente)) {
            JOptionPane.showMessageDialog(this, "Equipamento registado com sucesso!");
            txtNomeEquip_n.setText("");
            txtMac_n.setText("");
            txtSala_n.setText(""); // Limpa a sala
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao gravar na base de dados.");
        }
    }

    // Função Auxiliar para não repetir código
    private int extrairIdDaCombo(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return -1;
        try {
            return Integer.parseInt(combo.getSelectedItem().toString().split(" - ")[0]);
        } catch (Exception e) {
            return -1;
        }
    }
    
    //
    private void preencherCombos() {
        jbd db = new jbd();
        try {
        	ResultSet rs = db.getTodosClientes();
            // Limpar as duas combos de clientes
            cbCliente_n.removeAllItems();
            cbCliente_e.removeAllItems();
            cbCliente_n.addItem("0 - Selecione...");
            cbCliente_e.addItem("0 - Selecione...");
            while (rs != null && rs.next()) {
                String item = rs.getInt("id_cliente") + " - " + rs.getString("nome_cliente");
                cbCliente_n.addItem(item);
                cbCliente_e.addItem(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    private void preencherComboNodosFiltrados(int idCliente) {
        cbNodo_n.removeAllItems(); // Limpa a lista atual
        jbd db = new jbd();
        try {
            ResultSet rs = db.getNodosPorCliente(idCliente);
            while (rs != null && rs.next()) {
                cbNodo_n.addItem(rs.getInt("id_nodo") + " - " + rs.getString("nome_nodo"));
            }
            
            if (cbNodo_n.getItemCount() == 0) {
                cbNodo_n.addItem("0 - Nenhum Nodo encontrado para este cliente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void montarAbaEliminar(JPanel panel) {
        JLabel lblCli = new JLabel("1. Selecione o Cliente:");
        lblCli.setBounds(50, 40, 150, 20);
        panel.add(lblCli);

        cbCliente_e = new JComboBox<>();
        cbCliente_e.setBounds(200, 40, 250, 30);
        panel.add(cbCliente_e);

        JLabel lblEquip = new JLabel("2. Selecione o Equipamento:");
        lblEquip.setBounds(50, 100, 150, 20);
        panel.add(lblEquip);

        cbEquipamento_e = new JComboBox<>();
        cbEquipamento_e.setBounds(200, 100, 250, 30);
        panel.add(cbEquipamento_e);

        JButton btnEliminar = new JButton("Remover Equipamento");
        btnEliminar.setBackground(new Color(204, 0, 0));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setBounds(200, 180, 180, 40);
        panel.add(btnEliminar);
        // Quando muda o cliente, carrega os equipamentos dele
        cbCliente_e.addActionListener(e -> {
            int idCli = extrairIdDaCombo(cbCliente_e);
            if (idCli > 0) {preencherEquipamentosEliminar(idCli);
            }
        });

        btnEliminar.addActionListener(e -> acaoEliminarEquipamento());
    }
    private void preencherEquipamentosEliminar(int idCliente) {
        cbEquipamento_e.removeAllItems();
        jbd db = new jbd();
        try {
            ResultSet rs = db.getEquipamentosPorCliente(idCliente);
            while (rs != null && rs.next()) {
                cbEquipamento_e.addItem(rs.getInt("id_equip") + " - " + rs.getString("nome_dispositivo"));
            }
            if (cbEquipamento_e.getItemCount() == 0) {
                cbEquipamento_e.addItem("0 - Nenhum equipamento encontrado");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void acaoEliminarEquipamento() {
        int idEquip = extrairIdDaCombo(cbEquipamento_e);
        
        if (idEquip <= 0) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento válido.");
            return;
        }

        String nome = cbEquipamento_e.getSelectedItem().toString().split(" - ")[1];

        int confirmar = JOptionPane.showConfirmDialog(this, 
            "Tem a certeza que deseja eliminar o equipamento: " + nome + "?",
            "Confirmar Eliminação", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmar == JOptionPane.YES_OPTION) {
            jbd db = new jbd();
            if (db.eliminarEquipamento(idEquip)) {
                JOptionPane.showMessageDialog(this, "Equipamento removido com sucesso!");
                preencherEquipamentosEliminar(extrairIdDaCombo(cbCliente_e));
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao eliminar o equipamento.");
            }
        }
    }
}