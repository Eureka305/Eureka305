import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.io.*;
import javax.swing.undo.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;


public class Notepad extends JFrame implements ActionListener,DocumentListener
{
    JMenu fileMenu,editMenu,formatMenu,viewMenu,helpMenu;
    JPopupMenu popupMenu;
    JMenuItem popupMenu_Undo,popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,popupMenu_Delete,popupMenu_SelectAll;
    JMenuItem fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,fileMenu_Print,fileMenu_Exit;
    JMenuItem editMenu_Undo,editMenu_Cut,editMenu_Copy,editMenu_Paste,editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_Replace,editMenu_SelectAll,editMenu_TimeDate;
    JCheckBoxMenuItem formatMenu_LineWrap;
    JMenuItem formatMenu_Font;
    JCheckBoxMenuItem viewMenu_Status;
    JMenuItemhelpMenu_AboutNotepad;
    JTextArea editArea;
    JLabel statusLabel;
    Toolkit toolkit=Toolkit.getDefaultToolkit();
    Clipboard clipBoard=toolkit.getSystemClipboard();
    protected UndoManager undo=new UndoManager();
    protected UndoableEditListener undoHandler=new UndoHandler();
    String oldValue;
    boolean isNewFile=true;
    File currentFile;

    public Notepad()
    {
        super("JavaEditor");

        Font font = new Font("Dialog", Font.PLAIN, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }

        JMenuBar menuBar=new JMenuBar();

        fileMenu=new JMenu("file(F)");
        fileMenu.setMnemonic('F');

        fileMenu_New=new JMenuItem("new(N)");
        fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
        fileMenu_New.addActionListener(this);

        fileMenu_Open=new JMenuItem("open(O)...");
        fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
        fileMenu_Open.addActionListener(this);

        fileMenu_Save=new JMenuItem("save(S)");
        fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
        fileMenu_Save.addActionListener(this);

        fileMenu_SaveAs=new JMenuItem("save as(A)...");
        fileMenu_SaveAs.addActionListener(this);

        fileMenu_Print=new JMenuItem("print(P)...");
        fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        fileMenu_Print.addActionListener(this);

        fileMenu_Exit=new JMenuItem("exit(X)");
        fileMenu_Exit.addActionListener(this);

        editMenu=new JMenu("edit(E)");
        editMenu.setMnemonic('E');
        editMenu.addMenuListener(new MenuListener()
        {   public void menuCanceled(MenuEvent e)
        {   checkMenuItemEnabled();
        }
            public void menuDeselected(MenuEvent e)
            {   checkMenuItemEnabled();
            }
            public void menuSelected(MenuEvent e)
            {   checkMenuItemEnabled();
            }
        });

        editMenu_Undo=new JMenuItem("undo(U)");
        editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
        editMenu_Undo.addActionListener(this);
        editMenu_Undo.setEnabled(false);

        editMenu_Cut=new JMenuItem("cut(T)");
        editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
        editMenu_Cut.addActionListener(this);

        editMenu_Copy=new JMenuItem("copy(C)");
        editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
        editMenu_Copy.addActionListener(this);

        editMenu_Paste=new JMenuItem("past(P)");
        editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
        editMenu_Paste.addActionListener(this);

        editMenu_Delete=new JMenuItem("delete(D)");
        editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        editMenu_Delete.addActionListener(this);

        editMenu_Find=new JMenuItem("find(F)...");
        editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
        editMenu_Find.addActionListener(this);

        editMenu_FindNext=new JMenuItem("find next(N)");
        editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
        editMenu_FindNext.addActionListener(this);

        editMenu_Replace = new JMenuItem("replace(R)...",'R');
        editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        editMenu_Replace.addActionListener(this);


        editMenu_SelectAll = new JMenuItem("selectall",'A');
        editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        editMenu_SelectAll.addActionListener(this);

        editMenu_TimeDate = new JMenuItem("time/date(D)",'D');
        editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));
        editMenu_TimeDate.addActionListener(this);


        formatMenu=new JMenu("formate(O)");
        formatMenu.setMnemonic('O');

        formatMenu_LineWrap=new JCheckBoxMenuItem("auto swap line(W)");
        formatMenu_LineWrap.setMnemonic('W');
        formatMenu_LineWrap.setState(true);
        formatMenu_LineWrap.addActionListener(this);



        viewMenu=new JMenu("find(V)");
        viewMenu.setMnemonic('V');

        viewMenu_Status=new JCheckBoxMenuItem("status(S)");
        viewMenu_Status.setMnemonic('S');
        viewMenu_Status.setState(true);
        viewMenu_Status.addActionListener(this);


        helpMenu = new JMenu("help(H)");
        helpMenu.setMnemonic('H');


        helpMenu_AboutNotepad = new JMenuItem("about editor(A)");
        helpMenu_AboutNotepad.addActionListener(this);


        menuBar.add(fileMenu);
        fileMenu.add(fileMenu_New);
        fileMenu.add(fileMenu_Open);
        fileMenu.add(fileMenu_Save);
        fileMenu.add(fileMenu_SaveAs);
        fileMenu.addSeparator();
        fileMenu.add(fileMenu_Print);
        fileMenu.addSeparator();
        fileMenu.add(fileMenu_Exit);


        menuBar.add(editMenu);
        editMenu.add(editMenu_Undo);
        editMenu.addSeparator();
        editMenu.add(editMenu_Cut);
        editMenu.add(editMenu_Copy);
        editMenu.add(editMenu_Paste);
        editMenu.add(editMenu_Delete);
        editMenu.addSeparator();
        editMenu.add(editMenu_Find);
        editMenu.add(editMenu_FindNext);
        editMenu.add(editMenu_Replace);
        editMenu.addSeparator();
        editMenu.add(editMenu_SelectAll);
        editMenu.add(editMenu_TimeDate);


        menuBar.add(formatMenu);
        formatMenu.add(formatMenu_LineWrap);
        formatMenu.add(formatMenu_Font);


        menuBar.add(viewMenu);
        viewMenu.add(viewMenu_Status);


        menuBar.add(helpMenu);
        helpMenu.add(helpMenu_HelpTopics);
        helpMenu.addSeparator();
        helpMenu.add(helpMenu_AboutNotepad);


        this.setJMenuBar(menuBar);


        editArea=new JTextArea(20,50);
        JScrollPane scroller=new JScrollPane(editArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroller,BorderLayout.CENTER);
        editArea.setWrapStyleWord(true);
        editArea.setLineWrap(true);
        //this.add(editArea,BorderLayout.CENTER);
        oldValue=editArea.getText();


        editArea.getDocument().addUndoableEditListener(undoHandler);
        editArea.getDocument().addDocumentListener(this);


        popupMenu=new JPopupMenu();
        popupMenu_Undo=new JMenuItem("undo(U)");
        popupMenu_Cut=new JMenuItem("cut(T)");
        popupMenu_Copy=new JMenuItem("copy(C)");
        popupMenu_Paste=new JMenuItem("past(P)");
        popupMenu_Delete=new JMenuItem("delete(D)");
        popupMenu_SelectAll=new JMenuItem("select all(A)");

        popupMenu_Undo.setEnabled(false);


        popupMenu.add(popupMenu_Undo);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_Cut);
        popupMenu.add(popupMenu_Copy);
        popupMenu.add(popupMenu_Paste);
        popupMenu.add(popupMenu_Delete);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_SelectAll);


        popupMenu_Undo.addActionListener(this);
        popupMenu_Cut.addActionListener(this);
        popupMenu_Copy.addActionListener(this);
        popupMenu_Paste.addActionListener(this);
        popupMenu_Delete.addActionListener(this);
        popupMenu_SelectAll.addActionListener(this);


        editArea.addMouseListener(new MouseAdapter()
        {   public void mousePressed(MouseEvent e)
        {   if(e.isPopupTrigger())
        {   popupMenu.show(e.getComponent(),e.getX(),e.getY());
        }
            checkMenuItemEnabled();
            editArea.requestFocus();
        }
            public void mouseReleased(MouseEvent e)
            {   if(e.isPopupTrigger())
            {   popupMenu.show(e.getComponent(),e.getX(),e.getY());
            }
                checkMenuItemEnabled();
                editArea.requestFocus();
            }
        });


        statusLabel=new JLabel("　F1 for help");
        this.add(statusLabel,BorderLayout.SOUTH);


        this.setLocation(100,100);
        this.setSize(650,550);
        this.setVisible(true);

        addWindowListener(new WindowAdapter()
        {   public void windowClosing(WindowEvent e)
        {   exitWindowChoose();
        }
        });

        checkMenuItemEnabled();
        editArea.requestFocus();
    }


    public void checkMenuItemEnabled()
    {   String selectText=editArea.getSelectedText();
        if(selectText==null)
        {   editMenu_Cut.setEnabled(false);
            popupMenu_Cut.setEnabled(false);
            editMenu_Copy.setEnabled(false);
            popupMenu_Copy.setEnabled(false);
            editMenu_Delete.setEnabled(false);
            popupMenu_Delete.setEnabled(false);
        }
        else
        {   editMenu_Cut.setEnabled(true);
            popupMenu_Cut.setEnabled(true);
            editMenu_Copy.setEnabled(true);
            popupMenu_Copy.setEnabled(true);
            editMenu_Delete.setEnabled(true);
            popupMenu_Delete.setEnabled(true);
        }

        Transferable contents=clipBoard.getContents(this);
        if(contents==null)
        {   editMenu_Paste.setEnabled(false);
            popupMenu_Paste.setEnabled(false);
        }
        else
        {   editMenu_Paste.setEnabled(true);
            popupMenu_Paste.setEnabled(true);
        }
    }


    public void exitWindowChoose()
    {   editArea.requestFocus();
        String currentValue=editArea.getText();
        if(currentValue.equals(oldValue)==true)
        {   System.exit(0);
        }
        else
        {   int exitChoose=JOptionPane.showConfirmDialog(this,"would you like to save the file ？","exit",JOptionPane.YES_NO_CANCEL_OPTION);
            if(exitChoose==JOptionPane.YES_OPTION)
            {   //boolean isSave=false;
                if(isNewFile)
                {
                    String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setApproveButtonText("comferm");
                    fileChooser.setDialogTitle("save as");

                    int result=fileChooser.showSaveDialog(this);

                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText("　you have not save the file");
                        return;
                    }

                    File saveFileName=fileChooser.getSelectedFile();

                    if(saveFileName==null||saveFileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(this,"ilegal file name","ilegal file name",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileWriter fw=new FileWriter(saveFileName);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        fw.close();

                        isNewFile=false;
                        currentFile=saveFileName;
                        oldValue=editArea.getText();

                        this.setTitle(saveFileName.getName()+"  - editor");
                        statusLabel.setText("　opening file:"+saveFileName.getAbsoluteFile());

                    }
                    catch(IOException ioException){
                    }
                    }
                }
                else
                {
                    try
                    {   FileWriter fw=new FileWriter(currentFile);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        fw.close();

                    }
                    catch(IOException ioException){
                    }
                }
                System.exit(0);

            }
            else if(exitChoose==JOptionPane.NO_OPTION)
            {   System.exit(0);
            }
            else
            {   return;
            }
        }
    }
    
    public void find()
    {   final JDialog findDialog=new JDialog(this,"find",false);
        Container con=findDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel findContentLabel=new JLabel("find content(N)：");
        final JTextField findText=new JTextField(15);
        JButton findNextButton=new JButton("find next(F)：");
        final JCheckBox matchCheckBox=new JCheckBox("match check(C)");
        ButtonGroup bGroup=new ButtonGroup();
        final JRadioButton upButton=new JRadioButton("up(U)");
        final JRadioButton downButton=new JRadioButton("down(U)");
        downButton.setSelected(true);
        bGroup.add(upButton);
        bGroup.add(downButton);

        JButton cancel=new JButton("cancel");
        cancel.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {   findDialog.dispose();
        }
        });

        findNextButton.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {
            int k=0,m=0;
            final String str1,str2,str3,str4,strA,strB;
            str1=editArea.getText();
            str2=findText.getText();
            str3=str1.toUpperCase();
            str4=str2.toUpperCase();
            if(matchCheckBox.isSelected())
            {   strA=str1;
                strB=str2;
            }
            else
            {   strA=str3;
                strB=str4;
            }
            if(upButton.isSelected())
            {   //k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
                if(editArea.getSelectedText()==null)
                    k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
                else
                    k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);
                if(k>-1)
                {   //String strData=strA.subString(k,strB.getText().length()+1);
                    editArea.setCaretPosition(k);
                    editArea.select(k,k+strB.length());
                }
                else
                {   JOptionPane.showMessageDialog(null,"unable to find the content","find",JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else if(downButton.isSelected())
            {   if(editArea.getSelectedText()==null)
                k=strA.indexOf(strB,editArea.getCaretPosition()+1);
            else
                k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);
                if(k>-1)
                {   //String strData=strA.subString(k,strB.getText().length()+1);
                    editArea.setCaretPosition(k);
                    editArea.select(k,k+strB.length());
                }
                else
                {   JOptionPane.showMessageDialog(null,"unable to find the content","find",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        });
        JPanel panel1=new JPanel();
        JPanel panel2=new JPanel();
        JPanel panel3=new JPanel();
        JPanel directionPanel=new JPanel();
        directionPanel.setBorder(BorderFactory.createTitledBorder("direction"));
        directionPanel.add(upButton);
        directionPanel.add(downButton);
        panel1.setLayout(new GridLayout(2,1));
        panel1.add(findNextButton);
        panel1.add(cancel);
        panel2.add(findContentLabel);
        panel2.add(findText);
        panel2.add(panel1);
        panel3.add(matchCheckBox);
        panel3.add(directionPanel);
        con.add(panel2);
        con.add(panel3);
        findDialog.setSize(410,180);
        findDialog.setResizable(false);
        findDialog.setLocation(230,280);
        findDialog.setVisible(true);
    }


    public void replace()
    {   final JDialog replaceDialog=new JDialog(this,"replace",false);
        Container con=replaceDialog.getContentPane();//返回此对话框的contentPane对象
        con.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel findContentLabel=new JLabel("find content(N)：");
        final JTextField findText=new JTextField(15);
        JButton findNextButton=new JButton("find next(F):");
        JLabel replaceLabel=new JLabel("replace with(P)：");
        final JTextField replaceText=new JTextField(15);
        JButton replaceButton=new JButton("replace(R)");
        JButton replaceAllButton=new JButton("replace all(A)");
        JButton cancel=new JButton("cancel");
        cancel.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {   replaceDialog.dispose();
        }
        });
        final JCheckBox matchCheckBox=new JCheckBox("match check(C)");
        ButtonGroup bGroup=new ButtonGroup();
        final JRadioButton upButton=new JRadioButton("up(U)");
        final JRadioButton downButton=new JRadioButton("down (U)");
        downButton.setSelected(true);
        bGroup.add(upButton);
        bGroup.add(downButton);

        findNextButton.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {
            int k=0,m=0;
            final String str1,str2,str3,str4,strA,strB;
            str1=editArea.getText();
            str2=findText.getText();
            str3=str1.toUpperCase();
            str4=str2.toUpperCase();
            if(matchCheckBox.isSelected())
            {   strA=str1;
                strB=str2;
            }
            else
            {   strA=str3;
                strB=str4;
            }
            if(upButton.isSelected())
            {
                if(editArea.getSelectedText()==null)
                    k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
                else
                    k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);
                if(k>-1)
                {
                    editArea.setCaretPosition(k);
                    editArea.select(k,k+strB.length());
                }
                else
                {   JOptionPane.showMessageDialog(null,"unable to find the content","find",JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else if(downButton.isSelected())
            {   if(editArea.getSelectedText()==null)
                k=strA.indexOf(strB,editArea.getCaretPosition()+1);
            else
                k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);
                if(k>-1)
                {   //String strData=strA.subString(k,strB.getText().length()+1);
                    editArea.setCaretPosition(k);
                    editArea.select(k,k+strB.length());
                }
                else
                {   JOptionPane.showMessageDialog(null,"unable to find the content","find",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        });

        replaceButton.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {   if(replaceText.getText().length()==0 && editArea.getSelectedText()!=null)
            editArea.replaceSelection("");
            if(replaceText.getText().length()>0 && editArea.getSelectedText()!=null)
                editArea.replaceSelection(replaceText.getText());
        }
        });

        
        replaceAllButton.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {   editArea.setCaretPosition(0); 
            int k=0,m=0,replaceCount=0;
            if(findText.getText().length()==0)
            {   JOptionPane.showMessageDialog(replaceDialog,"please input finding items","warning",JOptionPane.WARNING_MESSAGE);
                findText.requestFocus(true);
                return;
            }
            while(k>-1)
                final String str1,str2,str3,str4,strA,strB;
                str1=editArea.getText();
                str2=findText.getText();
                str3=str1.toUpperCase();
                str4=str2.toUpperCase();
                if(matchCheckBox.isSelected())
                {   strA=str1;
                    strB=str2;
                }
                else
                {   strA=str3;
                    strB=str4;
                }
                if(upButton.isSelected())
                {
                    if(editArea.getSelectedText()==null)
                        k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
                    else
                        k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);
                    if(k>-1)
                    {
                        editArea.setCaretPosition(k);
                        editArea.select(k,k+strB.length());
                    }
                    else
                    {   if(replaceCount==0)
                    {   JOptionPane.showMessageDialog(replaceDialog, "unable to find the content", "editor",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {   JOptionPane.showMessageDialog(replaceDialog,"replaced"+replaceCount+"content!","replaced",JOptionPane.INFORMATION_MESSAGE);
                    }
                    }
                }
                else if(downButton.isSelected())
                {   if(editArea.getSelectedText()==null)
                    k=strA.indexOf(strB,editArea.getCaretPosition()+1);
                else
                    k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);
                    if(k>-1)
                    {
                        editArea.setCaretPosition(k);
                        editArea.select(k,k+strB.length());
                    }
                    else
                    {   if(replaceCount==0)
                    {   JOptionPane.showMessageDialog(replaceDialog, "unable to find the content", "editor",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {   JOptionPane.showMessageDialog(replaceDialog,"replaced "+replaceCount+"content!","replaced",JOptionPane.INFORMATION_MESSAGE);
                    }
                    }
                }
                if(replaceText.getText().length()==0 && editArea.getSelectedText()!= null)
                {   editArea.replaceSelection("");
                    replaceCount++;
                }

                if(replaceText.getText().length()>0 && editArea.getSelectedText()!= null)
                {   editArea.replaceSelection(replaceText.getText());
                    replaceCount++;
                }
            }
        }
        });


        JPanel directionPanel=new JPanel();
        directionPanel.setBorder(BorderFactory.createTitledBorder("direction"));
        directionPanel.add(upButton);
        directionPanel.add(downButton);
        JPanel panel1=new JPanel();
        JPanel panel2=new JPanel();
        JPanel panel3=new JPanel();
        JPanel panel4=new JPanel();
        panel4.setLayout(new GridLayout(2,1));
        panel1.add(findContentLabel);
        panel1.add(findText);
        panel1.add(findNextButton);
        panel4.add(replaceButton);
        panel4.add(replaceAllButton);
        panel2.add(replaceLabel);
        panel2.add(replaceText);
        panel2.add(panel4);
        panel3.add(matchCheckBox);
        panel3.add(directionPanel);
        panel3.add(cancel);
        con.add(panel1);
        con.add(panel2);
        con.add(panel3);
        replaceDialog.setSize(420,220);
        replaceDialog.setResizable(false);
        replaceDialog.setLocation(230,280);
        replaceDialog.setVisible(true);
    }


    public void font()
    {   final JDialog fontDialog=new JDialog(this,"字体设置",false);
        Container con=fontDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel fontLabel=new JLabel("字体(F)：");
        fontLabel.setPreferredSize(new Dimension(100,20));//构造一个Dimension，并将其初始化为指定宽度和高度
        JLabel styleLabel=new JLabel("字形(Y)：");
        styleLabel.setPreferredSize(new Dimension(100,20));
        JLabel sizeLabel=new JLabel("size(S)：");
        sizeLabel.setPreferredSize(new Dimension(100,20));
        final JLabel sample=new JLabel("Michael's Notepad");
        //sample.setHorizontalAlignment(SwingConstants.CENTER);
        final JTextField fontText=new JTextField(9);
        fontText.setPreferredSize(new Dimension(200,20));
        final JTextField styleText=new JTextField(8);
        styleText.setPreferredSize(new Dimension(200,20));
        final int style[]={Font.PLAIN,Font.BOLD,Font.ITALIC,Font.BOLD+Font.ITALIC};
        final JTextField sizeText=new JTextField(5);
        sizeText.setPreferredSize(new Dimension(200,20));
        JButton okButton=new JButton("ok");
        JButton cancel=new JButton("cancel");
        cancel.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {   fontDialog.dispose();
        }
        });
        Font currentFont=editArea.getFont();
        fontText.setText(currentFont.getFontName());
        fontText.selectAll();
        if(currentFont.getStyle()==Font.PLAIN)
            styleText.setText("常规");
        else if(currentFont.getStyle()==Font.BOLD)
            styleText.setText("粗体");
        else if(currentFont.getStyle()==Font.ITALIC)
            styleText.setText("斜体");
        else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
            styleText.setText("粗斜体");
        styleText.selectAll();
        String str=String.valueOf(currentFont.getSize());
        sizeText.setText(str);
        sizeText.selectAll();
        final JList fontList,styleList,sizeList;
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String fontName[]=ge.getAvailableFontFamilyNames();
        fontList=new JList(fontName);
        fontList.setFixedCellWidth(86);
        fontList.setFixedCellHeight(20);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final String fontStyle[]={"常规","粗体","斜体","粗斜体"};
        styleList=new JList(fontStyle);
        styleList.setFixedCellWidth(86);
        styleList.setFixedCellHeight(20);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if(currentFont.getStyle()==Font.PLAIN)
            styleList.setSelectedIndex(0);
        else if(currentFont.getStyle()==Font.BOLD)
            styleList.setSelectedIndex(1);
        else if(currentFont.getStyle()==Font.ITALIC)
            styleList.setSelectedIndex(2);
        else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
            styleList.setSelectedIndex(3);
        final String fontSize[]={"8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72"};
        sizeList=new JList(fontSize);
        sizeList.setFixedCellWidth(43);
        sizeList.setFixedCellHeight(20);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(new ListSelectionListener()
        {   public void valueChanged(ListSelectionEvent event)
        {   fontText.setText(fontName[fontList.getSelectedIndex()]);
            fontText.selectAll();
            Font sampleFont1=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
            sample.setFont(sampleFont1);
        }
        });
        styleList.addListSelectionListener(new ListSelectionListener()
        {   public void valueChanged(ListSelectionEvent event)
        {   int s=style[styleList.getSelectedIndex()];
            styleText.setText(fontStyle[s]);
            styleText.selectAll();
            Font sampleFont2=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
            sample.setFont(sampleFont2);
        }
        });
        sizeList.addListSelectionListener(new ListSelectionListener()
        {   public void valueChanged(ListSelectionEvent event)
        {   sizeText.setText(fontSize[sizeList.getSelectedIndex()]);
            //sizeText.requestFocus();
            sizeText.selectAll();
            Font sampleFont3=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
            sample.setFont(sampleFont3);
        }
        });
        okButton.addActionListener(new ActionListener()
        {   public void actionPerformed(ActionEvent e)
        {   Font okFont=new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
            editArea.setFont(okFont);
            fontDialog.dispose();
        }
        });
        JPanel samplePanel=new JPanel();
        samplePanel.setBorder(BorderFactory.createTitledBorder(""));
        samplePanel.add(sample);
        JPanel panel1=new JPanel();
        JPanel panel2=new JPanel();
        JPanel panel3=new JPanel();
        panel2.add(fontText);
        panel2.add(styleText);
        panel2.add(sizeText);
        panel2.add(okButton);
        panel3.add(new JScrollPane(fontList));
        panel3.add(new JScrollPane(styleList));
        panel3.add(new JScrollPane(sizeList));
        panel3.add(cancel);
        con.add(panel1);
        con.add(panel2);
        con.add(panel3);
        con.add(samplePanel);
        fontDialog.setSize(350,340);
        fontDialog.setLocation(200,200);
        fontDialog.setResizable(false);
        fontDialog.setVisible(true);
    }



    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==fileMenu_New)
        {   editArea.requestFocus();
            String currentValue=editArea.getText();
            boolean isTextChange=(currentValue.equals(oldValue))?false:true;
            if(isTextChange)
            {   int saveChoose=JOptionPane.showConfirmDialog(this,"this file have not been saved would like to save it","warning",JOptionPane.YES_NO_CANCEL_OPTION);
                if(saveChoose==JOptionPane.YES_OPTION)
                {   String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setDialogTitle("save as");
                    int result=fileChooser.showSaveDialog(this);
                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText("you have not choose a file");
                        return;
                    }
                    File saveFileName=fileChooser.getSelectedFile();
                    if(saveFileName==null || saveFileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(this,"ilegal file name","ilegal file name",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileWriter fw=new FileWriter(saveFileName);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        bfw.close();
                        isNewFile=false;
                        currentFile=saveFileName;
                        oldValue=editArea.getText();
                        this.setTitle(saveFileName.getName()+" - editor");
                        statusLabel.setText("opening file："+saveFileName.getAbsoluteFile());
                    }
                    catch (IOException ioException)
                    {
                    }
                    }
                }
                else if(saveChoose==JOptionPane.NO_OPTION)
                {   editArea.replaceRange("",0,editArea.getText().length());
                    statusLabel.setText(" new file");
                    this.setTitle("untitled - editor");
                    isNewFile=true;
                    undo.discardAllEdits(); 
                    editMenu_Undo.setEnabled(false);
                    oldValue=editArea.getText();
                }
                else if(saveChoose==JOptionPane.CANCEL_OPTION)
                {   return;
                }
            }
            else
            {   editArea.replaceRange("",0,editArea.getText().length());
                statusLabel.setText(" new file");
                this.setTitle("untitled - editor");
                isNewFile=true;
                undo.discardAllEdits();
                editMenu_Undo.setEnabled(false);
                oldValue=editArea.getText();
            }
        }
        
        else if(e.getSource()==fileMenu_Open)
        {   editArea.requestFocus();
            String currentValue=editArea.getText();
            boolean isTextChange=(currentValue.equals(oldValue))?false:true;
            if(isTextChange)
            {   int saveChoose=JOptionPane.showConfirmDialog(this,"this file have not been saved would like to save it","warning",JOptionPane.YES_NO_CANCEL_OPTION);
                if(saveChoose==JOptionPane.YES_OPTION)
                {   String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setDialogTitle("save as");
                    int result=fileChooser.showSaveDialog(this);
                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText("you have not choose a file");
                        return;
                    }
                    File saveFileName=fileChooser.getSelectedFile();
                    if(saveFileName==null || saveFileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(this,"ilegal file name","ilegal file name",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileWriter fw=new FileWriter(saveFileName);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        bfw.close();
                        isNewFile=false;
                        currentFile=saveFileName;
                        oldValue=editArea.getText();
                        this.setTitle(saveFileName.getName()+" - editor");
                        statusLabel.setText("opening file："+saveFileName.getAbsoluteFile());
                    }
                    catch (IOException ioException)
                    {
                    }
                    }
                }
                else if(saveChoose==JOptionPane.NO_OPTION)
                {   String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setDialogTitle("one file");
                    int result=fileChooser.showOpenDialog(this);
                    if(result==JFileChooser.CANCEL_OPTION)
                    {   statusLabel.setText("you have not choose a file");
                        return;
                    }
                    File fileName=fileChooser.getSelectedFile();
                    if(fileName==null || fileName.getName().equals(""))
                    {   JOptionPane.showMessageDialog(this,"ilegal file name","ilegal file name",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {   try
                    {   FileReader fr=new FileReader(fileName);
                        BufferedReader bfr=new BufferedReader(fr);
                        editArea.setText("");
                        while((str=bfr.readLine())!=null)
                        {   editArea.append(str);
                        }
                        this.setTitle(fileName.getName()+" - editor");
                        statusLabel.setText(" opening file："+fileName.getAbsoluteFile());
                        fr.close();
                        isNewFile=false;
                        currentFile=fileName;
                        oldValue=editArea.getText();
                    }
                    catch (IOException ioException)
                    {
                    }
                    }
                }
                else
                {   return;
                }
            }
            else
            {   String str=null;
                JFileChooser fileChooser=new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("one file");
                int result=fileChooser.showOpenDialog(this);
                if(result==JFileChooser.CANCEL_OPTION)
                {   statusLabel.setText(" you have not shoose a file ");
                    return;
                }
                File fileName=fileChooser.getSelectedFile();
                if(fileName==null || fileName.getName().equals(""))
                {   JOptionPane.showMessageDialog(this,"ilegal file name","ilegal file name",JOptionPane.ERROR_MESSAGE);
                }
                else
                {   try
                {   FileReader fr=new FileReader(fileName);
                    BufferedReader bfr=new BufferedReader(fr);
                    editArea.setText("");
                    while((str=bfr.readLine())!=null)
                    {   editArea.append(str);
                    }
                    this.setTitle(fileName.getName()+" - editor");
                    statusLabel.setText(" opening file："+fileName.getAbsoluteFile());
                    fr.close();
                    isNewFile=false;
                    currentFile=fileName;
                    oldValue=editArea.getText();
                }
                catch (IOException ioException)
                {
                }
                }
            }
        }
        else if(e.getSource()==fileMenu_Save)
        {   editArea.requestFocus();
            if(isNewFile)
            {   String str=null;
                JFileChooser fileChooser=new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("save");
                int result=fileChooser.showSaveDialog(this);
                if(result==JFileChooser.CANCEL_OPTION)
                {   statusLabel.setText("you have not save the file");
                    return;
                }
                File saveFileName=fileChooser.getSelectedFile();
                if(saveFileName==null || saveFileName.getName().equals(""))
                {   JOptionPane.showMessageDialog(this,"ilegal file name","ilegal file name",JOptionPane.ERROR_MESSAGE);
                }
                else
                {   try
                {   FileWriter fw=new FileWriter(saveFileName);
                    BufferedWriter bfw=new BufferedWriter(fw);
                    bfw.write(editArea.getText(),0,editArea.getText().length());
                    bfw.flush();
                    bfw.close();
                    isNewFile=false;
                    currentFile=saveFileName;
                    oldValue=editArea.getText();
                    this.setTitle(saveFileName.getName()+" - editor");
                    statusLabel.setText("current file："+saveFileName.getAbsoluteFile());
                }
                catch (IOException ioException)
                {
                }
                }
            }
            else
            {   try
            {   FileWriter fw=new FileWriter(currentFile);
                BufferedWriter bfw=new BufferedWriter(fw);
                bfw.write(editArea.getText(),0,editArea.getText().length());
                bfw.flush();
                fw.close();
            }
            catch(IOException ioException)
            {
            }
            }
        }
        else if(e.getSource()==fileMenu_SaveAs)
        {   editArea.requestFocus();
            String str=null;
            JFileChooser fileChooser=new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("save as");
            int result=fileChooser.showSaveDialog(this);
            if(result==JFileChooser.CANCEL_OPTION)
            {   statusLabel.setText("　please shoose a file");
                return;
            }
            File saveFileName=fileChooser.getSelectedFile();
            if(saveFileName==null||saveFileName.getName().equals(""))
            {   JOptionPane.showMessageDialog(this,"illegal name","ilage file name",JOptionPane.ERROR_MESSAGE);
            }
            else
            {   try
            {   FileWriter fw=new FileWriter(saveFileName);
                BufferedWriter bfw=new BufferedWriter(fw);
                bfw.write(editArea.getText(),0,editArea.getText().length());
                bfw.flush();
                fw.close();
                oldValue=editArea.getText();
                this.setTitle(saveFileName.getName()+"  - editor");
                statusLabel.setText("　opening file :"+saveFileName.getAbsoluteFile());
            }
            catch(IOException ioException)
            {
            }
            }
        }
        //print fixing
        else if(e.getSource()==fileMenu_Print)
        {   editArea.requestFocus();
            JOptionPane.showMessageDialog(this,"fixing","warning",JOptionPane.WARNING_MESSAGE);
        }
        else if(e.getSource()==fileMenu_Exit)
        {   int exitChoose=JOptionPane.showConfirmDialog(this,"Do you want to exit ?","exit",JOptionPane.OK_CANCEL_OPTION);
            if(exitChoose==JOptionPane.OK_OPTION)
            {   System.exit(0);
            }
            else
            {   return;
            }
        }
        else if(e.getSource()==editMenu_Undo || e.getSource()==popupMenu_Undo)
        {   editArea.requestFocus();
            if(undo.canUndo())
            {   try
            {   undo.undo();
            }
            catch (CannotUndoException ex)
            {   System.out.println("Unable to undo:" + ex);
                //ex.printStackTrace();
            }
            }
            if(!undo.canUndo())
            {   editMenu_Undo.setEnabled(false);
            }
        }
        else if(e.getSource()==editMenu_Cut || e.getSource()==popupMenu_Cut)
        {   editArea.requestFocus();
            String text=editArea.getSelectedText();
            StringSelection selection=new StringSelection(text);
            clipBoard.setContents(selection,null);
            editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
            checkMenuItemEnabled();
        }
        else if(e.getSource()==editMenu_Copy || e.getSource()==popupMenu_Copy)
        {   editArea.requestFocus();
            String text=editArea.getSelectedText();
            StringSelection selection=new StringSelection(text);
            clipBoard.setContents(selection,null);
            checkMenuItemEnabled();
        }
        else if(e.getSource()==editMenu_Paste || e.getSource()==popupMenu_Paste)
        {   editArea.requestFocus();
            Transferable contents=clipBoard.getContents(this);
            if(contents==null)return;
            String text="";
            try
            {   text=(String)contents.getTransferData(DataFlavor.stringFlavor);
            }
            catch (Exception exception)
            {
            }
            editArea.replaceRange(text,editArea.getSelectionStart(),editArea.getSelectionEnd());
            checkMenuItemEnabled();
        }
        else if(e.getSource()==editMenu_Delete || e.getSource()==popupMenu_Delete)
        {   editArea.requestFocus();
            editArea.replaceRange("",editArea.getSelectionStart(),editArea.getSelectionEnd());
            checkMenuItemEnabled();
        }
        else if(e.getSource()==editMenu_Find)
        {   editArea.requestFocus();
            find();
        }
        else if(e.getSource()==editMenu_FindNext)
        {   editArea.requestFocus();
            find();
        }
        else if(e.getSource()==editMenu_Replace)
        {   editArea.requestFocus();
            replace();
        }

        else if(e.getSource()==editMenu_TimeDate)
        {   editArea.requestFocus();
            //SimpleDateFormat currentDateTime=new SimpleDateFormat("HH:mmyyyy-MM-dd");
            //editArea.insert(currentDateTime.format(new Date()),editArea.getCaretPosition());
            Calendar rightNow=Calendar.getInstance();
            Date date=rightNow.getTime();
            editArea.insert(date.toString(),editArea.getCaretPosition());
        }
        else if(e.getSource()==editMenu_SelectAll || e.getSource()==popupMenu_SelectAll)
        {   editArea.selectAll();
        }
        else if(e.getSource()==formatMenu_LineWrap)
        {   if(formatMenu_LineWrap.getState())
            editArea.setLineWrap(true);
        else
            editArea.setLineWrap(false);

        }
        else if(e.getSource()==formatMenu_Font)
        {   editArea.requestFocus();
            font();
        }
        else if(e.getSource()==viewMenu_Status)
        {   if(viewMenu_Status.getState())
            statusLabel.setVisible(true);
        else
            statusLabel.setVisible(false);
        }
        else if(e.getSource()==helpMenu_HelpTopics)
        {   editArea.requestFocus();
            JOptionPane.showMessageDialog(this,"","",JOptionPane.INFORMATION_MESSAGE);
        }
        else if(e.getSource()==helpMenu_AboutNotepad)
        {   editArea.requestFocus();
            JOptionPane.showMessageDialog(this,
                    "----------------------------------------------\n"+
                            " Michael Zhang 21015027\n"+
                            "----------------------------------------------\n",
                    "Editor",JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void removeUpdate(DocumentEvent e)
    {   editMenu_Undo.setEnabled(true);
    }
    public void insertUpdate(DocumentEvent e)
    {   editMenu_Undo.setEnabled(true);
    }
    public void changedUpdate(DocumentEvent e)
    {   editMenu_Undo.setEnabled(true);
    }


    class UndoHandler implements UndoableEditListener
    {   public void undoableEditHappened(UndoableEditEvent uee)
    {   undo.addEdit(uee.getEdit());
    }
    }


    public static void main(String args[])
    {   Notepad notepad=new Notepad();
        notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使用 System exit 方法退出应用程序
    }
}