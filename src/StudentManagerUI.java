import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StudentManagerUI extends JFrame {
    private JTextField txtID, txtName, txtClass, txtGender, txtSubject1, txtSubject2, txtSubject3, txtSearch;
    private JComboBox<String> cbMajor;
    private JLabel lblSubject1, lblSubject2, lblSubject3;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final HashMap<String, String[]> subjectsMap = new HashMap<>();
    private ArrayList<Student> students = new ArrayList<>();
    private static final String DATA_FILE = "students.txt";

    public StudentManagerUI() {
        // Thiết lập cửa sổ chính
        setTitle("Student Manager");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        System.out.println("Initialize interface...");

        // Khởi tạo danh sách môn học theo chuyên ngành
        subjectsMap.put("IT", new String[]{"Software Engineering", "Computer Science", "Computer Networks and Data Communications"});
        subjectsMap.put("BIZ", new String[]{"Business Administration", "Marketing", "Finance and Banking"});
        subjectsMap.put("GD", new String[]{"Visual Identity Design", "Marketing & Advertising Design", "User Interface Design - UI"});

        // Tiêu đề
        JLabel lblTitle = new JLabel("Student Manager");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setBounds(400, 10, 400, 40);
        add(lblTitle);

        // Nhãn và trường nhập liệu
        JLabel lblID = new JLabel("Student ID");
        JLabel lblName = new JLabel("Student Name");
        JLabel lblClass = new JLabel("Class");
        JLabel lblGender = new JLabel("Gender");
        JLabel lblMajor = new JLabel("Major");
        lblSubject1 = new JLabel("Subject 1");
        lblSubject2 = new JLabel("Subject 2");
        lblSubject3 = new JLabel("Subject 3");

        txtID = new JTextField();
        txtName = new JTextField();
        txtClass = new JTextField();
        txtGender = new JTextField();
        cbMajor = new JComboBox<>(new String[]{"IT", "BIZ", "GD"});
        txtSubject1 = new JTextField();
        txtSubject2 = new JTextField();
        txtSubject3 = new JTextField();

        // Các nút chức năng
        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");
        JButton btnSave = new JButton("Save");

        // Định vị các thành phần
        int xLabel = 20, xField = 150, y = 70, height = 25;
        lblID.setBounds(xLabel, y, 120, height); txtID.setBounds(xField, y, 150, height); y += 30;
        lblName.setBounds(xLabel, y, 120, height); txtName.setBounds(xField, y, 150, height); y += 30;
        lblClass.setBounds(xLabel, y, 120, height); txtClass.setBounds(xField, y, 150, height); y += 30;
        lblGender.setBounds(xLabel, y, 120, height); txtGender.setBounds(xField, y, 150, height); y += 30;
        lblMajor.setBounds(xLabel, y, 120, height); cbMajor.setBounds(xField, y, 150, height); y += 30;
        lblSubject1.setBounds(xLabel, y, 160, height); txtSubject1.setBounds(xField, y, 150, height); y += 30;
        lblSubject2.setBounds(xLabel, y, 160, height); txtSubject2.setBounds(xField, y, 150, height); y += 30;
        lblSubject3.setBounds(xLabel, y, 160, height); txtSubject3.setBounds(xField, y, 150, height); y += 30;

        btnAdd.setBounds(xLabel, y + 30, 80, 25);
        btnDelete.setBounds(xLabel + 90, y + 30, 80, 25);
        btnUpdate.setBounds(xLabel + 180, y + 30, 80, 25);
        btnSave.setBounds(xLabel + 270, y + 30, 80, 25);

        // Thêm các thành phần vào cửa sổ
        add(lblID); add(txtID);
        add(lblName); add(txtName);
        add(lblClass); add(txtClass);
        add(lblGender); add(txtGender);
        add(lblMajor); add(cbMajor);
        add(lblSubject1); add(txtSubject1);
        add(lblSubject2); add(txtSubject2);
        add(lblSubject3); add(txtSubject3);
        add(btnAdd); add(btnDelete); add(btnUpdate); add(btnSave);

        // Trường tìm kiếm
        txtSearch = new JTextField();
        txtSearch.setBounds(450, 70, 200, 25);
        add(txtSearch);
        JLabel lblSearch = new JLabel("Search");
        lblSearch.setBounds(400, 70, 50, 25);
        add(lblSearch);

        // Bảng danh sách sinh viên
        String[] columns = {"Student ID", "Student Name", "Class", "Gender", "Major", "Avg", "Rank"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(320, 100, 650, 330);
        add(scrollPane);

        // Các nút sắp xếp
        JButton btnSortByID = new JButton("Sort by ID");
        JButton btnSortByAvg = new JButton("Sort by Avg");
        JButton btnSortByRank = new JButton("Sort by Rank");

        btnSortByID.setBounds(450, 440, 120, 25);
        btnSortByAvg.setBounds(580, 440, 120, 25);
        btnSortByRank.setBounds(710, 440, 120, 25);

        add(btnSortByID);
        add(btnSortByAvg);
        add(btnSortByRank);

        // Gắn sự kiện
        cbMajor.addActionListener(e -> updateSubjectLabels());
        btnAdd.addActionListener(e -> addStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnSave.addActionListener(e -> saveStudentsToList());

        btnSortByID.addActionListener(e -> sortByID());
        btnSortByAvg.addActionListener(e -> sortByAvg());
        btnSortByRank.addActionListener(e -> sortByRank());

        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchStudent(txtSearch.getText());
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelectedStudent();
            }
        });

        // Cập nhật nhãn môn học ban đầu
        updateSubjectLabels();

        // Tải dữ liệu từ file
        loadStudentsFromFile();
        System.out.println("Interface initialization complete.");
    }

    private void updateSubjectLabels() {
        String major = (String) cbMajor.getSelectedItem();
        String[] subjects = subjectsMap.get(major);
        if (subjects != null) {
            lblSubject1.setText(subjects[0]);
            lblSubject2.setText(subjects[1]);
            lblSubject3.setText(subjects[2]);
        }
    }

    private void addStudent() {
        try {
            String id = txtID.getText().trim();
            String name = txtName.getText().trim();
            String sClass = txtClass.getText().trim();
            String gender = txtGender.getText().trim();
            String major = cbMajor.getSelectedItem().toString();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID and Name cannot be left blank.");
                return;
            }

            // Kiểm tra ID trùng
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(id)) {
                    JOptionPane.showMessageDialog(this, "Student ID already exists.");
                    return;
                }
            }

            double s1 = txtSubject1.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSubject1.getText());
            double s2 = txtSubject2.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSubject2.getText());
            double s3 = txtSubject3.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSubject3.getText());

            if (s1 < 0 || s1 > 10 || s2 < 0 || s2 > 10 || s3 < 0 || s3 > 10) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 10.");
                return;
            }

            double avg = (s1 + s2 + s3) / 3;
            String rank = getRank(avg);

            tableModel.addRow(new Object[]{id, name, sClass, gender, major, String.format("%.2f", avg), rank});
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the subjects.");
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                String id = txtID.getText().trim();
                String name = txtName.getText().trim();
                String sClass = txtClass.getText().trim();
                String gender = txtGender.getText().trim();
                String major = cbMajor.getSelectedItem().toString();

                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Student ID and Name cannot be left blank.");
                    return;
                }

                double s1 = txtSubject1.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSubject1.getText());
                double s2 = txtSubject2.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSubject2.getText());
                double s3 = txtSubject3.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtSubject3.getText());

                if (s1 < 0 || s1 > 10 || s2 < 0 || s2 > 10 || s3 < 0 || s3 > 10) {
                    JOptionPane.showMessageDialog(this, "Score must be between 0 and 10.");
                    return;
                }

                double avg = (s1 + s2 + s3) / 3;
                String rank = getRank(avg);

                tableModel.setValueAt(id, row, 0);
                tableModel.setValueAt(name, row, 1);
                tableModel.setValueAt(sClass, row, 2);
                tableModel.setValueAt(gender, row, 3);
                tableModel.setValueAt(major, row, 4);
                tableModel.setValueAt(String.format("%.2f", avg), row, 5);
                tableModel.setValueAt(rank, row, 6);

                clearFields();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the subjects.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to update.");
        }
    }

    private void searchStudent(String keyword) {
        keyword = keyword.toLowerCase().trim();
        sorter.setSortKeys(null);
        if (keyword.isEmpty()) {
            table.clearSelection();
            return;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (id.contains(keyword) || name.contains(keyword)) {
                table.setRowSelectionInterval(i, i);
                return;
            }
        }
        table.clearSelection();
    }

    private void loadSelectedStudent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtID.setText(tableModel.getValueAt(row, 0).toString());
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            txtClass.setText(tableModel.getValueAt(row, 2).toString());
            txtGender.setText(tableModel.getValueAt(row, 3).toString());
            cbMajor.setSelectedItem(tableModel.getValueAt(row, 4).toString());

            txtSubject1.setText("");
            txtSubject2.setText("");
            txtSubject3.setText("");
        }
    }

    private void clearFields() {
        txtID.setText("");
        txtName.setText("");
        txtClass.setText("");
        txtGender.setText("");
        txtSubject1.setText("");
        txtSubject2.setText("");
        txtSubject3.setText("");
        table.clearSelection();
    }

    private String getRank(double avg) {
        if (avg >= 8.0) return "Distinction";
        else if (avg >= 6.5) return "Merit";
        else if (avg >= 5.0) return "Pass";
        else return "Fail";
    }

    // Hàm sắp xếp theo ID sử dụng Bubble Sort
    private void sortByID() {
        ArrayList<Student> tempStudents = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            String name = tableModel.getValueAt(i, 1).toString();
            String sClass = tableModel.getValueAt(i, 2).toString();
            String gender = tableModel.getValueAt(i, 3).toString();
            String major = tableModel.getValueAt(i, 4).toString();
            double avg = Double.parseDouble(tableModel.getValueAt(i, 5).toString());
            String rank = tableModel.getValueAt(i, 6).toString();
            tempStudents.add(new Student(id, name, sClass, gender, major, avg, rank));
        }

        // Bubble Sort theo ID
        for (int i = 0; i < tempStudents.size() - 1; i++) {
            for (int j = 0; j < tempStudents.size() - i - 1; j++) {
                if (tempStudents.get(j).id.compareTo(tempStudents.get(j + 1).id) > 0) {
                    Student temp = tempStudents.get(j);
                    tempStudents.set(j, tempStudents.get(j + 1));
                    tempStudents.set(j + 1, temp);
                }
            }
        }

        // Cập nhật lại tableModel
        tableModel.setRowCount(0);
        for (Student s : tempStudents) {
            tableModel.addRow(new Object[]{s.id, s.name, s.sClass, s.gender, s.major, String.format("%.2f", s.avg), s.rank});
        }
    }

    // Hàm sắp xếp theo Avg sử dụng Bubble Sort
    private void sortByAvg() {
        ArrayList<Student> tempStudents = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            String name = tableModel.getValueAt(i, 1).toString();
            String sClass = tableModel.getValueAt(i, 2).toString();
            String gender = tableModel.getValueAt(i, 3).toString();
            String major = tableModel.getValueAt(i, 4).toString();
            double avg = Double.parseDouble(tableModel.getValueAt(i, 5).toString());
            String rank = tableModel.getValueAt(i, 6).toString();
            tempStudents.add(new Student(id, name, sClass, gender, major, avg, rank));
        }

        // Bubble Sort theo Avg (giảm dần)
        for (int i = 0; i < tempStudents.size() - 1; i++) {
            for (int j = 0; j < tempStudents.size() - i - 1; j++) {
                if (tempStudents.get(j).avg < tempStudents.get(j + 1).avg) {
                    Student temp = tempStudents.get(j);
                    tempStudents.set(j, tempStudents.get(j + 1));
                    tempStudents.set(j + 1, temp);
                }
            }
        }

        // Cập nhật lại tableModel
        tableModel.setRowCount(0);
        for (Student s : tempStudents) {
            tableModel.addRow(new Object[]{s.id, s.name, s.sClass, s.gender, s.major, String.format("%.2f", s.avg), s.rank});
        }
    }

    // Hàm sắp xếp theo Rank sử dụng Bubble Sort
    private void sortByRank() {
        ArrayList<Student> tempStudents = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            String name = tableModel.getValueAt(i, 1).toString();
            String sClass = tableModel.getValueAt(i, 2).toString();
            String gender = tableModel.getValueAt(i, 3).toString();
            String major = tableModel.getValueAt(i, 4).toString();
            double avg = Double.parseDouble(tableModel.getValueAt(i, 5).toString());
            String rank = tableModel.getValueAt(i, 6).toString();
            tempStudents.add(new Student(id, name, sClass, gender, major, avg, rank));
        }

        // Bubble Sort theo Rank
        for (int i = 0; i < tempStudents.size() - 1; i++) {
            for (int j = 0; j < tempStudents.size() - i - 1; j++) {
                int rank1 = getRankValue(tempStudents.get(j).rank);
                int rank2 = getRankValue(tempStudents.get(j + 1).rank);
                if (rank1 > rank2) {
                    Student temp = tempStudents.get(j);
                    tempStudents.set(j, tempStudents.get(j + 1));
                    tempStudents.set(j + 1, temp);
                }
            }
        }

        // Cập nhật lại tableModel
        tableModel.setRowCount(0);
        for (Student s : tempStudents) {
            tableModel.addRow(new Object[]{s.id, s.name, s.sClass, s.gender, s.major, String.format("%.2f", s.avg), s.rank});
        }
    }

    // Hàm hỗ trợ để gán giá trị số cho Rank
    private int getRankValue(String rank) {
        switch (rank) {
            case "Distinction": return 1;
            case "Merit": return 2;
            case "Pass": return 3;
            case "Fail": return 4;
            default: return 5;
        }
    }

    private void saveStudentsToList() {
        students.clear();
        System.out.println("Start saving student list...");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String id = tableModel.getValueAt(i, 0).toString();
                String name = tableModel.getValueAt(i, 1).toString();
                String sClass = tableModel.getValueAt(i, 2).toString();
                String gender = tableModel.getValueAt(i, 3).toString();
                String major = tableModel.getValueAt(i, 4).toString();
                double avg = Double.parseDouble(tableModel.getValueAt(i, 5).toString());
                String rank = tableModel.getValueAt(i, 6).toString();

                Student student = new Student(id, name, sClass, gender, major, avg, rank);
                students.add(student);
                System.out.println("Add students: " + student);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Invalid GPA data in row " + (i + 1));
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Student s : students) {
                writer.write(String.format("%s,%s,%s,%s,%s,%.2f,%s%n",
                        s.id, s.name, s.sClass, s.gender, s.major, s.avg, s.rank));
            }
            System.out.println("Đã lưu " + students.size() + " sinh viên vào " + DATA_FILE);
            JOptionPane.showMessageDialog(this, "Saved " + students.size() + " student(s) to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Lỗi khi lưu file: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + e.getMessage());
        }
    }

    private void loadStudentsFromFile() {
        students.clear();
        tableModel.setRowCount(0);
        System.out.println("Start downloading data from " + DATA_FILE + "...");

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 7) {
                    try {
                        String id = parts[0];
                        String name = parts[1];
                        String sClass = parts[2];
                        String gender = parts[3];
                        String major = parts[4];
                        double avg = Double.parseDouble(parts[5]);
                        String rank = parts[6];

                        Student student = new Student(id, name, sClass, gender, major, avg, rank);
                        students.add(student);
                        tableModel.addRow(new Object[]{id, name, sClass, gender, major, String.format("%.2f", avg), rank});
                        System.out.println("Download student: " + student);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data in file: " + line);
                    }
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
            System.out.println("Loaded " + students.size() + " students from " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("File not found or cannot be read " + DATA_FILE + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagerUI ui = new StudentManagerUI();
            ui.setVisible(true);
        });
    }
}

class Student {
    String id, name, sClass, gender, major, rank;
    double avg;

    public Student(String id, String name, String sClass, String gender, String major, double avg, String rank) {
        this.id = id;
        this.name = name;
        this.sClass = sClass;
        this.gender = gender;
        this.major = major;
        this.avg = avg;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + sClass + " - " + gender + " - " + major + " - " + String.format("%.2f", avg) + " - " + rank;
    }
}