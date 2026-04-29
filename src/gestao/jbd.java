package gestao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JOptionPane;

//import com.mysql.cj.xdevapi.Statement;


import gestao.jbd;

public class jbd {
	
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	
	public void startTunnel() {
	    try {
	    	Process cleanUp = Runtime.getRuntime().exec("taskkill /f /im cloudflared.exe");
	        cleanUp.waitFor();
	        ProcessBuilder pb = new ProcessBuilder(
	            "./src/bin/cloudflared.exe", 
	            "access", "tcp", 
	            "--hostname", "[O teu tunel]", 
	            "--listener", "127.0.0.1:3307"
	        );

	        pb.inheritIO(); 

	        Process process = pb.start();

	        System.out.println("A iniciar ligação...");
	        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	            process.destroy();
	            System.out.println("Túnel encerrado automaticamente.");
	        }));
	        Thread.sleep(3000);
	        
	        System.out.println("Pronto para iniciar.");

	    } catch (Exception e) {
	        System.err.println("Erro ao iniciar o Tunel: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	
	public Connection ligar() {

	    String DB_URL = "jdbc:mysql://127.0.0.1:3307/gestao_redes";
	    String user = "admin";
	    String pass = "admin123";
	    
	    Connection connection = null;
	    
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        connection = DriverManager.getConnection(DB_URL, user, pass);
	        statement = connection.createStatement();
	        
	        System.out.println("Ligado com sucesso à base de dados via Túnel!");
	        
	        return connection;
	        
	    } catch (Exception e) {
	        System.out.println("erro de SQL: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public ResultSet getNiveis() {
        try {
            Connection c = this.ligar();
            return c.createStatement().executeQuery("SELECT * FROM niveis_acesso");
        } catch (Exception e) {
            return null;
        }
    }
	public boolean inserirUtilizador(String user, String pass, String nivelNome) {
	    try {
	        Connection c = this.ligar();
	        PreparedStatement psN = c.prepareStatement("SELECT id_nivel FROM niveis_acesso WHERE nome_nivel = ?");
	        psN.setString(1, nivelNome);
	        ResultSet rs = psN.executeQuery();
	        
	        if (rs.next()) {
	            int idNivel = rs.getInt("id_nivel");
	            String sql = "INSERT INTO utilizadores (username, password, id_nivel) VALUES (?, ?, ?)";
	            PreparedStatement psI = c.prepareStatement(sql);
	            psI.setString(1, user);
	            psI.setString(2, pass);
	            psI.setInt(3, idNivel);
	            return psI.executeUpdate() > 0;
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return false;
	}


	public ResultSet getUtilizadorPorId(int id) {
	    try {
	        Connection c = this.ligar();
	        String sql = "SELECT u.username, n.nome_nivel, c.nome_cliente " +
	                     "FROM utilizadores u " +
	                     "LEFT JOIN niveis_acesso n ON u.id_nivel = n.id_nivel " +
	                     "LEFT JOIN clientes c ON u.id_cliente_associado = c.id_cliente " + 
	                     "WHERE u.id_user = ?";
	        
	        PreparedStatement ps = c.prepareStatement(sql);
	        ps.setInt(1, id);
	        return ps.executeQuery();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
    public ResultSet getClientes() {
        try {
            Connection c = this.ligar();
            return c.createStatement().executeQuery("SELECT nome_cliente FROM clientes ORDER BY nome_cliente");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean atualizarUtilizador(int id, String user, String pass, String nivelNome, String clienteNome) {
        try {
            Connection c = this.ligar();
            
            // 1. Buscar o ID do Nível pelo nome
            int idNivel = -1;
            PreparedStatement psN = c.prepareStatement("SELECT id_nivel FROM niveis_acesso WHERE nome_nivel = ?");
            psN.setString(1, nivelNome);
            ResultSet rsN = psN.executeQuery();
            if (rsN.next()) idNivel = rsN.getInt("id_nivel");

            // 2. Buscar o ID do Cliente pelo nome (se houver um selecionado)
            Integer idCliente = null;
            if (!clienteNome.isEmpty()) {
                PreparedStatement psC = c.prepareStatement("SELECT id_cliente FROM clientes WHERE nome_cliente = ?");
                psC.setString(1, clienteNome);
                ResultSet rsC = psC.executeQuery();
                if (rsC.next()) idCliente = rsC.getInt("id_cliente");
            }

            // 3. Executar o UPDATE com a coluna correta: id_cliente_associado
            String sql = "UPDATE utilizadores SET username = ?, password = ?, id_nivel = ?, id_cliente_associado = ? WHERE id_user = ?";
            PreparedStatement psU = c.prepareStatement(sql);
            psU.setString(1, user);
            psU.setString(2, pass);
            psU.setInt(3, idNivel);
            
            // Se idCliente for null, gravamos NULL na BD
            if (idCliente != null) psU.setInt(4, idCliente); 
            else psU.setNull(4, java.sql.Types.INTEGER);
            
            psU.setInt(5, id);

            return psU.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getIdPeloUsername(String nome) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT id_user FROM utilizadores WHERE username = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_user");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 se não encontrar o utilizador
    }
    
 // Busca utilizadores que contenham o texto pesquisado
    public ResultSet getUtilizadoresFiltro(String filtro) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT id_user, username FROM utilizadores WHERE username LIKE ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, "%" + filtro + "%"); 
            return ps.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }

    // Apaga o utilizador pelo ID
    public boolean eliminarUtilizador(int id) {
        try {
            Connection c = this.ligar();
            String sql = "DELETE FROM utilizadores WHERE id_user = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Criar Novo Cliente
    public boolean inserirCliente(String nome, String contacto, String localidade) {
        try {
            Connection c = this.ligar();
            String sql = "INSERT INTO clientes (nome_cliente, contacto, localidade) VALUES (?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, contacto);
            ps.setString(3, localidade);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Listar Clientes
    
    public ResultSet getTodosClientes() {
        try {
            Connection c = this.ligar();
            // Ordenamos por nome para ser mais fácil de encontrar na lista
            String sql = "SELECT id_cliente, nome_cliente, contacto, localidade FROM clientes ORDER BY nome_cliente ASC";
            Statement st = c.createStatement();
            return st.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Atualizar Cliente Existente
    
    public boolean atualizarCliente(int id, String nome, String contacto, String localidade) {
        try {
            Connection c = this.ligar();
            System.out.println("A tentar update no ID " + id + " com Nome: " + nome);
            String sql = "UPDATE clientes SET nome_cliente = ?, contacto = ?, localidade = ? WHERE id_cliente = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, contacto);
            ps.setString(3, localidade);
            ps.setInt(4, id);
            int linhasAfetadas = ps.executeUpdate();
            
            // OUTRA LINHA DE DEBUG:
            System.out.println("Linhas alteradas: " + linhasAfetadas);
            
            return linhasAfetadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Eliminar Cliente
    
    public boolean eliminarCliente(int id) {
        try {
            Connection c = this.ligar();
            String sql = "DELETE FROM clientes WHERE id_cliente = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            
            e.printStackTrace();
            return false;
        }
    }
    
    // Filtrar Clientes
    
    public ResultSet getClientesFiltro(String filtro) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT id_cliente, nome_cliente FROM clientes WHERE nome_cliente LIKE ? ORDER BY nome_cliente ASC";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, "%" + filtro + "%");
            return ps.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }
    
     // Criar Novo Equipamento
    public boolean inserirEquipamento(String nome, String mac, String sala, int idNodo, int idCliente) {
        try {
        	Connection c = this.ligar();
            String sql = "INSERT INTO equipamentos_ligados (nome_dispositivo, mac_address, sala, id_nodo, id_cliente) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, mac);
            ps.setString(3, sala);
            
            // Se idNodo ou idCliente puderem ser nulos, usamos setNull
            if (idNodo > 0) ps.setInt(4, idNodo); else ps.setNull(3, java.sql.Types.INTEGER);
            if (idCliente > 0) ps.setInt(5, idCliente); else ps.setNull(4, java.sql.Types.INTEGER);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 // Retorna o total de portas configurado para o nodo
    public int getTotalPortas(int idNodo) {
        try {
            Connection c = this.ligar();
            PreparedStatement ps = c.prepareStatement("SELECT total_portas FROM nodos WHERE id_nodo = ?");
            ps.setInt(1, idNodo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total_portas");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // Soma equipamentos + outros nodos pendurados neste nodo
    public int getPortasOcupadas(int idNodo) {
        int contagem = 0;
        try {
            Connection c = this.ligar();
            
            // Contar equipamentos
            PreparedStatement ps1 = c.prepareStatement("SELECT COUNT(*) FROM equipamentos_ligados WHERE id_nodo = ?");
            ps1.setInt(1, idNodo);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) contagem += rs1.getInt(1);
            
            // Contar outros nodos (sub-switches)
            PreparedStatement ps2 = c.prepareStatement("SELECT COUNT(*) FROM nodos WHERE id_pai = ?");
            ps2.setInt(1, idNodo);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) contagem += rs2.getInt(1);
            
        } catch (Exception e) { e.printStackTrace(); }
        return contagem;
    }

 // Criar um novo Nodo (Router, Switch, etc)
    public boolean inserirNodo(String nome, String tipo, int portas, int idPai, String ipGestao, int idCliente) {
        try {
            Connection c = this.ligar();
            String sql = "INSERT INTO nodos (nome_nodo, tipo, total_portas, id_pai, ip_gestao, id_cliente) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, tipo);
            ps.setInt(3, portas);
            
            if (idPai <= 0) ps.setNull(4, java.sql.Types.INTEGER);
            else ps.setInt(4, idPai);
            
            ps.setString(5, ipGestao);
            
            if (idCliente <= 0) ps.setNull(6, java.sql.Types.INTEGER);
            else ps.setInt(6, idCliente);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Listar para a ComboBox (ID - Nome)
    public ResultSet getTodosNodos() {
        try {
            Connection c = this.ligar();
            return c.createStatement().executeQuery("SELECT id_nodo, nome_nodo FROM nodos ORDER BY nome_nodo ASC");
        } catch (Exception e) { return null; }
    }

    // Listar so Nodo de cliente expecifico 
    public ResultSet getNodosPorCliente(int idCliente) {
    	try {
    		Connection c = this.ligar();
    		String sql = "SELECT id_nodo, nome_nodo FROM nodos WHERE id_cliente = ? ORDER BY nome_nodo ASC";
    		PreparedStatement ps = c.prepareStatement(sql);
    		ps.setInt(1, idCliente);
    		return ps.executeQuery();
    	} catch (Exception e) {
    		return null;
    	}
    }
    
 // Listar equipamentos de um cliente específico
    public ResultSet getEquipamentosPorCliente(int idCliente) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT id_equip, nome_dispositivo FROM equipamentos_ligados WHERE id_cliente = ? ORDER BY nome_dispositivo ASC";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idCliente);
            return ps.executeQuery();
        } catch (Exception e) { return null; }
    }

    // Apagar o equipamento
    public boolean eliminarEquipamento(int idEquip) {
        try {
            Connection c = this.ligar();
            String sql = "DELETE FROM equipamentos_ligados WHERE id_equip = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idEquip);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    // Apagar os Nodo
    public boolean eliminarNodo(int idNodo) {
        try {
            Connection c = this.ligar();
            String sql = "DELETE FROM nodos WHERE id_nodo = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idNodo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
        
    // Gerar inventario geral em arvore

 // Obter todos os nodos de um cliente que NÃO têm pai (os Routers principais)
    public ResultSet getNodosRaizPorCliente(int idCli) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT * FROM nodos WHERE id_cliente = ? AND (id_pai IS NULL OR id_pai = 0)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idCli);
            return ps.executeQuery();
        } catch (Exception e) { return null; }
    }

    // Obter os "filhos" de um nodo específico (outros switches/APs)
    public ResultSet getNodosFilhos(int idPai) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT * FROM nodos WHERE id_pai = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPai);
            return ps.executeQuery();
        } catch (Exception e) { return null; }
    }

    // Obter equipamentos ligados diretamente a um nodo
    public ResultSet getEquipamentosPorNodo(int idNodo) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT nome_dispositivo, mac_address FROM equipamentos_ligados WHERE id_nodo = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idNodo);
            return ps.executeQuery();
        } catch (Exception e) { return null; }
    }
    
    
    // Pesquisa tanto no nome como no MAC ao mesmo tempo
    public ResultSet pesquisarEquipamento(String termo) {
        try {
            Connection c = this.ligar();
            String sql = "SELECT e.id_equip, e.nome_dispositivo, e.mac_address, e.sala, n.nome_nodo, c.nome_cliente " +
                         "FROM equipamentos_ligados e " +
                         "INNER JOIN nodos n ON e.id_nodo = n.id_nodo " +
                         "INNER JOIN clientes c ON e.id_cliente = c.id_cliente " +
                         "WHERE e.nome_dispositivo LIKE ? OR e.mac_address LIKE ? " +
                         "ORDER BY e.nome_dispositivo ASC";
            
            PreparedStatement ps = c.prepareStatement(sql);
            String busca = "%" + termo + "%"; 
            ps.setString(1, busca);
            ps.setString(2, busca);
            
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //Gerar relatorios
    public ResultSet getDadosRelatorio() {
        try {
            Connection c = this.ligar();
            String sql = "SELECT c.nome_cliente, n.nome_nodo, n.tipo, e.nome_dispositivo, e.mac_address, e.sala " +
                         "FROM equipamentos_ligados e " +
                         "JOIN nodos n ON e.id_nodo = n.id_nodo " +
                         "JOIN clientes c ON e.id_cliente = c.id_cliente " +
                         "ORDER BY c.nome_cliente, n.nome_nodo";
            return c.createStatement().executeQuery(sql);
        } catch (Exception e) { return null; }
    }
}