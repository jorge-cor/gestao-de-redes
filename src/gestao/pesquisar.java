package gestao;

import java.awt.BorderLayout;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

public class pesquisar extends JFrame {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtBusca;

    public pesquisar(Connection conn) {
        setTitle("Localizar Equipamento (Nome/MAC)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 850, 500);
        
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);
        JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPane.add(panelBusca, BorderLayout.NORTH);
        
        panelBusca.add(new JLabel("Texto a pesquisar:"));
        txtBusca = new JTextField(25);
        panelBusca.add(txtBusca);
        
        JButton btnIr = new JButton("Procurar");
        panelBusca.add(btnIr);
        modelo = new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Dispositivo", "MAC Address", "Sala", "Nodo (Switch)", "Cliente"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
      
        JScrollPane scrollPane = new JScrollPane(tabela);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        btnIr.addActionListener(e -> realizarPesquisa());
        
        // Pesquisar ao carregar no Enter
        txtBusca.addActionListener(e -> realizarPesquisa());
    }

    private void realizarPesquisa() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduza um nome ou endereço MAC.");
            return;
        }

        modelo.setRowCount(0); // Limpa resultados anteriores
        jbd db = new jbd();
        
        try {
            ResultSet rs = db.pesquisarEquipamento(termo);
            int contador = 0;
            
            while (rs != null && rs.next()) {
                modelo.addRow(new Object[] {
                    rs.getInt("id_equip"),
                    rs.getString("nome_dispositivo"),
                    rs.getString("mac_address"),
                    rs.getString("sala"),
                    rs.getString("nome_nodo"),
                    rs.getString("nome_cliente")
                });
                contador++;
            }
            
            if (contador == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum registo encontrado para: " + termo);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}