package gestao;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.sql.*;

public class inventario extends JFrame {
    private JTree arvore;

    public inventario(Connection conn) {
        setTitle("Mapa Hierárquico da Rede");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 600);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // Nó raiz que não aparece ou serve de título geral
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Infraestrutura Global");
        
        carregarArvore(raiz);

        arvore = new JTree(raiz);
        JScrollPane scrollPane = new JScrollPane(arvore);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarArvore(DefaultMutableTreeNode raiz) {
        jbd db = new jbd();
        try {
            // 1. Primeiro nível: Clientes
            ResultSet rsCli = db.getTodosClientes();
            while (rsCli.next()) {
                int idCli = rsCli.getInt("id_cliente");
                DefaultMutableTreeNode nodoCliente = new DefaultMutableTreeNode(rsCli.getString("nome_cliente"));
                raiz.add(nodoCliente);

                // 2. Iniciar a cascata de Nodos (Começa pelos que não têm pai)
                ResultSet rsNodosRaiz = db.getNodosRaizPorCliente(idCli);
                while (rsNodosRaiz.next()) {
                    adicionarNodoERecursivos(nodoCliente, rsNodosRaiz, db);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Método Recursivo que constrói a "árvore real"
    private void adicionarNodoERecursivos(DefaultMutableTreeNode paiNode, ResultSet rsAtual, jbd db) throws SQLException {
        int idNodo = rsAtual.getInt("id_nodo");
        String nomeNodo = rsAtual.getString("nome_nodo");
        String tipo = rsAtual.getString("tipo");

        // Cria o ramo do Nodo (Ex: Switch 1)
        DefaultMutableTreeNode esteNodo = new DefaultMutableTreeNode(nomeNodo + " [" + tipo + "]");
        paiNode.add(esteNodo);

        // A. Adicionar Equipamentos finais ligados a este Nodo
        ResultSet rsEquips = db.getEquipamentosPorNodo(idNodo);
        while (rsEquips.next()) {
            String info = rsEquips.getString("nome_dispositivo") + " (" + rsEquips.getString("mac_address") + ")";
            esteNodo.add(new DefaultMutableTreeNode(info));
        }

        // B. RECURSIVIDADE: Procurar outros Nodos que tenham este como pai
        ResultSet rsFilhos = db.getNodosFilhos(idNodo);
        while (rsFilhos.next()) {
            adicionarNodoERecursivos(esteNodo, rsFilhos, db);
        }
    }
}