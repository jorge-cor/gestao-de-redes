package gestao;

import java.awt.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class relatorios extends JFrame {
    private JTable tabela;
    private DefaultTableModel modelo;

    public relatorios(Connection conn) {
        setTitle("Centro de Relatórios e Exportação");
        setBounds(100, 100, 900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        setContentPane(contentPane);


        modelo = new DefaultTableModel(new Object[][] {}, 
            new String[] {"Cliente", "Nodo", "Tipo Nodo", "Dispositivo", "MAC", "Sala"});
        tabela = new JTable(modelo);
        contentPane.add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        JPanel panelBotoes = new JPanel();
        contentPane.add(panelBotoes, BorderLayout.SOUTH);

        JButton btnCarregar = new JButton("Gerar Relatório");
        panelBotoes.add(btnCarregar);

        JButton btnExportar = new JButton("Exportar para Excel (CSV)");
        btnExportar.setBackground(new Color(34, 139, 34)); 
        btnExportar.setForeground(Color.WHITE);
        panelBotoes.add(btnExportar);

        btnCarregar.addActionListener(e -> carregarDados());
        btnExportar.addActionListener(e -> exportarCSV());

        carregarDados(); 
        }

    private void carregarDados() {
        modelo.setRowCount(0);
        jbd db = new jbd();
        try {
            ResultSet rs = db.getDadosRelatorio();
            while (rs != null && rs.next()) {
                modelo.addRow(new Object[] {
                    rs.getString("nome_cliente"), rs.getString("nome_nodo"),
                    rs.getString("tipo"), rs.getString("nome_dispositivo"),
                    rs.getString("mac_address"), rs.getString("sala")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void exportarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Relatório");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            if (!arquivo.getName().endsWith(".csv")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
                for (int i = 0; i < modelo.getColumnCount(); i++) {
                    bw.write(modelo.getColumnName(i) + (i == modelo.getColumnCount() - 1 ? "" : ";"));
                }
                bw.newLine();
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    for (int j = 0; j < modelo.getColumnCount(); j++) {
                        bw.write(modelo.getValueAt(i, j).toString() + (j == modelo.getColumnCount() - 1 ? "" : ";"));
                    }
                    bw.newLine();
                }

                JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso para:\n" + arquivo.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar: " + e.getMessage());
            }
        }
    }
}