package Presentacion.Ventanas;

import Presentacion.Recursos.RoundedPanel;
import Presentacion.Recursos.UITheme;
import Logica.Servicios.SessionContext;
import Logica.Servicios.AuditoriaService;
import Logica.DAO.UsuarioDAO;
import Logica.DAO.UsuarioRolDAO;
import Logica.Servicios.PasswordUtil;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class VentanaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JCheckBox chkRecordar;
    private JButton btnTogglePassword;
    private boolean passwordVisible = false;

    public VentanaLogin() {
        setTitle("SCTET - Login");
        setSize(780, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, UITheme.LOGIN_TOP,
                        0, getHeight(), UITheme.LOGIN_BOTTOM
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        RoundedPanel shell = new RoundedPanel(22);
        shell.setPanelBackground(new Color(255, 255, 255, 18));
        shell.setPanelBorder(new Color(255, 255, 255, 55));
        shell.setLayout(new GridLayout(1, 2, 0, 0));
        shell.setPreferredSize(new Dimension(760, 430));
        shell.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        shell.add(construirBannerIzquierdo());
        shell.add(construirCardLogin());

        root.add(shell, new GridBagConstraints());
        getRootPane().setDefaultButton(null);
    }

    private JPanel construirBannerIzquierdo() {
        RoundedPanel left = new RoundedPanel(18);
        left.setPanelBackground(new Color(255, 255, 255, 10));
        left.setPanelBorder(new Color(255, 255, 255, 35));
        left.setLayout(new BorderLayout());
        left.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("SCTET", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "<html><div style='text-align:center;'>Sistema de Control de Transporte</div></html>",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        top.add(title);
        top.add(Box.createVerticalStrut(6));
        top.add(subtitle);

        left.add(top, BorderLayout.NORTH);

        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setVerticalAlignment(SwingConstants.CENTER);

        URL url = getClass().getResource("/Presentacion/Recursos/login_banner.png");
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            autoScaleToLabel(img, original);
        } else {
            img.setText("<html><div style='text-align:center;'>"
                    + "<b>Bienvenido</b><br/>"
                    + "<span style='font-size:11px;'>Accede con tus credenciales para continuar</span>"
                    + "</div></html>");
            img.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            img.setForeground(new Color(255, 255, 255, 220));
        }
        left.add(img, BorderLayout.CENTER);

        //JLabel footer = new JLabel("<html><div style='text-align:center;'>"
          //      + "Soporte: Lun a Vie 08h00 - 20h00<br/>"
          //      + "+593 9XX XXX XXX"
          //      + "</div></html>");
       // footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
       // footer.setForeground(new Color(255, 255, 255, 200));
       // footer.setHorizontalAlignment(SwingConstants.CENTER);

     //   left.add(footer, BorderLayout.SOUTH);
        return left;
    }

    private void autoScaleToLabel(JLabel target, ImageIcon original) {
        Runnable apply = () -> {
            int w = target.getWidth();
            int h = target.getHeight();
            if (w <= 0 || h <= 0) return;
            Image scaled = original.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            target.setIcon(new ImageIcon(scaled));
        };

        SwingUtilities.invokeLater(apply);
        target.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                apply.run();
            }
        });
    }

    private JPanel construirCardLogin() {
        RoundedPanel card = new RoundedPanel(18);
        card.setPanelBackground(new Color(255, 255, 255, 235));
        card.setPanelBorder(new Color(255, 255, 255, 235));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("Bienvenido de nuevo");
        title.setFont(UITheme.H1);
        title.setForeground(UITheme.TEXT);

        JLabel subtitle = new JLabel("Ingresa tus credenciales para continuar");
        subtitle.setFont(UITheme.BODY);
        subtitle.setForeground(UITheme.MUTED);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(UITheme.SMALL);
        lblUser.setForeground(UITheme.MUTED);

        txtUsuario = new JTextField();
        styleInput(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(UITheme.SMALL);
        lblPass.setForeground(UITheme.MUTED);

        // ✅ NUEVO: Panel que contiene el password field + botón de ojito
        JPanel passwordPanel = crearPanelContraseñaConToggle();

        card.add(lblUser);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsuario);
        card.add(Box.createVerticalStrut(12));

        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordPanel);  // ✅ Usar el panel en vez del campo directamente
        card.add(Box.createVerticalStrut(12));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        chkRecordar = new JCheckBox("Recordar sesión");
        chkRecordar.setOpaque(false);
        chkRecordar.setFont(UITheme.SMALL);
        chkRecordar.setForeground(UITheme.MUTED);

        JLabel link = linkLabel("¿");
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(card,
                        "Recuperación de contraseña (prototipo).",
                        "Recuperar", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        row.add(chkRecordar, BorderLayout.WEST);
        row.add(link, BorderLayout.EAST);

        card.add(row);
        card.add(Box.createVerticalStrut(16));

        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setForeground(Color.black);
        btnLogin.setBackground(UITheme.PRIMARY);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> autenticar());

        getRootPane().setDefaultButton(btnLogin);

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(16));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        card.add(sep);
        card.add(Box.createVerticalStrut(12));

        JLabel note = new JLabel("<html><div style='text-align:center;'>"
                + "¿No tienes acceso? <u>Contacta al administrador</u>"
                + "</div></html>");
        note.setFont(UITheme.SMALL);
        note.setForeground(UITheme.MUTED);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);

        note.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(card,
                        "Soporte (prototipo): soporte@sctet.ec",
                        "", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        card.add(note);

        return card;
    }

    // ========================================
    // ✅ NUEVO: PANEL CON PASSWORD Y TOGGLE
    // ========================================
    private JPanel crearPanelContraseñaConToggle() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Crear el password field
        txtClave = new JPasswordField();
        txtClave.setFont(UITheme.BODY);
        txtClave.setForeground(UITheme.TEXT);
        txtClave.setBackground(Color.WHITE);
        txtClave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 35)  // Espacio a la derecha para el botón
        ));

        // Crear el botón toggle (ojito)
        btnTogglePassword = new JButton();
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setBorderPainted(false);
        btnTogglePassword.setContentAreaFilled(false);
        btnTogglePassword.setOpaque(false);
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.setPreferredSize(new Dimension(30, 30));
        
        // Intentar cargar iconos, si no existen usar emojis
        actualizarIconoToggle();
        
        // Acción del botón
        btnTogglePassword.addActionListener(e -> togglePasswordVisibility());

        // Panel que contiene el botón, posicionado sobre el campo
        JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));
        toggleContainer.setOpaque(false);
        toggleContainer.add(btnTogglePassword);

        // Agregar componentes
        panel.add(txtClave, BorderLayout.CENTER);
        panel.add(toggleContainer, BorderLayout.EAST);

        return panel;
    }

    // ========================================
    // ✅ TOGGLE PASSWORD VISIBILITY
    // ========================================
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            // Mostrar contraseña como texto normal
            txtClave.setEchoChar((char) 0);
        } else {
            // Ocultar contraseña (usar el carácter por defecto)
            txtClave.setEchoChar('●');
        }
        
        actualizarIconoToggle();
    }

    // ========================================
    // ✅ ACTUALIZAR ICONO DEL TOGGLE
    // ========================================
    private void actualizarIconoToggle() {
        // Intentar cargar iconos desde recursos
        String iconPath = passwordVisible 
            ? "/Presentacion/Recursos/icons/eye_open.png"
            : "/Presentacion/Recursos/icons/eye_closed.png";
        
        ImageIcon icon = cargarIcono(iconPath, 18, 18);
        
        if (icon != null) {
            btnTogglePassword.setIcon(icon);
            btnTogglePassword.setText("");
        } else {
            // Fallback: usar emojis si no hay iconos
            btnTogglePassword.setIcon(null);
            btnTogglePassword.setText(passwordVisible ? "👁️" : "👁️‍🗨️");
            btnTogglePassword.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        }
        
        btnTogglePassword.setToolTipText(
            passwordVisible ? "Ocultar contraseña" : "Mostrar contraseña"
        );
    }

    // ========================================
    // ✅ CARGAR ICONO
    // ========================================
    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) return null;
            
            Image img = new ImageIcon(url).getImage();
            Image scaled = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    private void styleInput(JComponent c) {
        c.setFont(UITheme.BODY);
        c.setForeground(UITheme.TEXT);
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JLabel linkLabel(String text) {
        JLabel l = new JLabel("<html><u>" + text + "</u></html>");
        l.setFont(UITheme.SMALL);
        l.setForeground(UITheme.PRIMARY);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return l;
    }

    private void autenticar() {
        try {
            String loginIngresado = txtUsuario.getText().trim();
            String claveIngresada = new String(txtClave.getPassword());

            // 1. Datos vacíos → datos inválidos
            if (loginIngresado.isEmpty() || claveIngresada.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo iniciar sesión: los datos ingresados no son válidos.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            UsuarioRolDAO usuarioRolDAO = new UsuarioRolDAO();

            // 2. Usuario existe
            long idUsuario = usuarioDAO.obtenerIdPorLogin(loginIngresado);
            if (idUsuario == 0) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo iniciar sesión: el nombre de usuario no existe.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UsuarioDAO.UsuarioRow u = usuarioDAO.obtenerPorId(idUsuario);

            // 3. Usuario activo
            if (!u.activo) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo iniciar sesión: los datos ingresados no son válidos.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Validar contraseña
            UsuarioDAO.PasswordData pd = usuarioDAO.obtenerHashSalt(idUsuario);
            String hashIngresado = PasswordUtil.hashSHA256(claveIngresada, pd.salt);

            if (!hashIngresado.equalsIgnoreCase(pd.hash)) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo iniciar sesión: la contraseña no corresponde al usuario.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 5. Obtener roles
            java.util.List<String> roles = usuarioRolDAO.listarRolesDeUsuario(idUsuario);
            String rolPrincipal = roles.contains("AdministradorDelSistema")
                    ? "AdministradorDelSistema"
                    : (!roles.isEmpty() ? roles.get(0) : "SIN_ROL");

            // 6. INICIAR SESIÓN
            SessionContext.iniciarSesion(
                idUsuario,       // Long - ID del usuario
                u.login,         // String - Login
                u.nombre,        // String - Nombre completo
                rolPrincipal     // String - Rol
            );

            AuditoriaService.registrar(
                    "Sistema", "LOGIN", "OK",
                    "Login exitoso. Usuario=" + u.login + ", Rol=" + rolPrincipal
            );

            // 7. Mensaje exigido por el ERS
            JOptionPane.showMessageDialog(this,
                    "Inicio de sesión exitoso.",
                    "Acceso permitido",
                    JOptionPane.INFORMATION_MESSAGE);

            // 8. Forzar cambio de contraseña (rgs3)
            if (u.requiereCambioClave) {
                VentanaCambioContrasena vc =
                        new VentanaCambioContrasena(this, idUsuario);
                vc.setVisible(true);

                UsuarioDAO.UsuarioRow actualizado =
                        usuarioDAO.obtenerPorId(idUsuario);

                if (actualizado.requiereCambioClave) {
                    return;
                }
            }

            // 9. Entrar al sistema
            VentanaPrincipal vp = new VentanaPrincipal(
                    SessionContext.getUsuarioLogin(),
                    SessionContext.getRol()
            );
            vp.setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al iniciar sesión:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}