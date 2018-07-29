package core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import moudles.Course;
import moudles.CourseClass;
import moudles.Professor;
import moudles.Room;
import moudles.Schedule;

public class DataUtil {

    public File mClassesFile;
    public File mProfsFreeTimesFile;
    public File mProfSkillsFile;
    public File mCapacityFile;
    public File mAskedClassesFile;
    public File mChartFile;
    public File mResultFile;
    public File mProfTextFile;
    public File mFitnessFile;

    public DataUtil(File classesFile, File profsFreeTimesFile, File profSkillsFile, File capacityFile, File askedClassesFile, File chartFile, File resultFile, File profTextFile, File fitnessFile) {
        mClassesFile = classesFile;
        mProfsFreeTimesFile = profsFreeTimesFile;
        mProfSkillsFile = profSkillsFile;
        mCapacityFile = capacityFile;
        mAskedClassesFile = askedClassesFile;
        mChartFile = chartFile;
        mResultFile = resultFile;
        mProfTextFile = profTextFile;
        mFitnessFile = fitnessFile;
    }

    public List<Professor> getProfessors() {
        FileInputStream fileInputStream = null;
        List<Professor> professors = new ArrayList<Professor>();

        try {
            File file = mProfsFreeTimesFile;
            fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);

                short[][] data = new short[6][5];

                int[] daysIndexes = {1, 2, 3, 4, 5, 6};

                for (int i = 0; i < daysIndexes.length; i++) {
                    XSSFRow row = currentSheet.getRow(daysIndexes[i]);
                    for (int cellIndex = 1; cellIndex < 6; cellIndex++) {
                        XSSFCell cell = row.getCell(cellIndex);
                        short numericCellValue = (short) cell.getNumericCellValue();
                        data[i][cellIndex - 1] = numericCellValue;
                    }
                }

                Professor professor = new Professor();
                professor.setProfId(sheetIndex);
                professor.setProfName(currentSheet.getSheetName().trim());
                professor.setProfFreeTime(data);
                professors.add(professor);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeFileStream(fileInputStream);
        }
        return professors;

    }

    public List<Course> getCourses() {
        FileInputStream fileInputStream = null;
        List<Course> courses = new ArrayList<>();

        try {
            File file = mProfSkillsFile;
            fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet currentSheet = workbook.getSheetAt(0);

            int rowIndex = 0;
            Iterator<Row> rowIterator = currentSheet.rowIterator();
            while (rowIterator.hasNext()) {
                int cellIndex = 0;
                Row currentRow = rowIterator.next();
                Iterator<Cell> cellIterator = currentRow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell != null) {
                        if (rowIndex == 0) {
                            courses.add(new Course(cellIndex, cell.getStringCellValue()));
                        }
                    }

                    cellIndex++;
                }

                rowIndex++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeFileStream(fileInputStream);
        }

        return courses;
    }

    public void setSeatsNeedForCourses(List<Course> courses) {
        FileInputStream askedInputStream = null;
        try {

            askedInputStream = new FileInputStream(mAskedClassesFile);
            XSSFWorkbook askedWorkbook = new XSSFWorkbook(askedInputStream);

            int count = 0;
            Iterator<Row> rowIterator = askedWorkbook.getSheetAt(0).iterator();
            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> iterator = nextRow.iterator();
                String courseName = iterator.next().getStringCellValue();
                boolean isNeedGreaterThanTwentySeats = (iterator.next().getStringCellValue().charAt(0)) == '0' ? true : false;

                for (Course course : courses) {
                    if (course.getName().equals(courseName)) {
                        course.setNeedGreaterThanTwentySeats(isNeedGreaterThanTwentySeats);
                        break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(askedInputStream);
        }

    }

    public void setStudentGroupForCourses(List<Course> courses) {
        FileInputStream inputStream = null;
        try {

            inputStream = new FileInputStream(mChartFile);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            int rowCount = 0;
            Iterator<Row> rowIterator = workbook.getSheetAt(0).iterator();
            List<String> terms = new ArrayList<>();
            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                if (rowCount == 0) {
                    Iterator<Cell> cellIterator = nextRow.iterator();
                    while (cellIterator.hasNext()) {
                        String termName = cellIterator.next().getStringCellValue();
                        terms.add(termName);
                    }
                } else {
                    for (int i = 0; i < nextRow.getLastCellNum(); i++) {
                        Cell cell = nextRow.getCell(i);
                        if (cell == null) continue;
                        String courseName = cell.getStringCellValue();
                        String termName = terms.get(i);

                        for (Course course : courses) {
                            if (course.getName().toLowerCase().equals(courseName.toLowerCase())) {
                                course.setStudentGroup(termName);
                                break;
                            }
                        }
                    }
                }

                rowCount++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(inputStream);
        }
    }

    public void setProfessorsSkills(List<Professor> professors) {
        FileInputStream fileInputStream = null;
        try {
            File file = mProfSkillsFile;
            fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet currentSheet = workbook.getSheetAt(0);

            int rowCount = currentSheet.getLastRowNum() - currentSheet.getFirstRowNum() + 1;
            for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
                XSSFRow row = currentSheet.getRow(rowIndex);
                int cellCount = row.getLastCellNum();

                String profName = row.getCell(0).getStringCellValue().trim();
                short[] haveSkills = new short[cellCount - 1];

                for (int cellIndex = 1; cellIndex < cellCount; cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);
                    short haveSkill = (short) cell.getNumericCellValue();
                    haveSkills[cellIndex - 1] = haveSkill;
                    for (Professor professor : professors) {
                        if (profName.equals(professor.getProfName())) {
                            professor.setProfSkills(haveSkills);
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(fileInputStream);
        }
    }

    public List<Room> getRooms() {
        List<Room> rooms = new ArrayList<Room>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(mClassesFile);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                XSSFSheet currentSheet = workbook.getSheetAt(sheetIndex);

                short[][] data = new short[6][5];

                int[] indexes = {1, 2, 3, 4, 5, 6};

                for (int i = 0; i < indexes.length; i++) {
                    XSSFRow row = currentSheet.getRow(indexes[i]);
                    for (int cellIndex = 1; cellIndex < 6; cellIndex++) {
                        XSSFCell cell = row.getCell(cellIndex);
                        short numericCellValue = (short) cell.getNumericCellValue();
                        data[i][cellIndex - 1] = numericCellValue;
                    }
                }

                Room room = new Room();
                room.setId(currentSheet.getSheetName());
                room.setFreeTimes(data);
                rooms.add(room);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(fileInputStream);
        }

        return rooms;
    }

    public void setCapacityForRooms(List<Room> rooms) {
        FileInputStream capacityInputStream = null;
        try {

            capacityInputStream = new FileInputStream(mCapacityFile);
            XSSFWorkbook classCapacityWorkbook = new XSSFWorkbook(capacityInputStream);

            int count = 0;
            Iterator<Row> rowIterator = classCapacityWorkbook.getSheetAt(0).iterator();
            while (rowIterator.hasNext()) {
                if (count == 0) {
                    count++;
                    continue;
                }
                Row nextRow = rowIterator.next();
                Iterator<Cell> iterator = nextRow.iterator();
                String classId = iterator.next().getStringCellValue();
                boolean isCapacityGreaterThanTwenty = (iterator.next().getStringCellValue().charAt(0)) == '0' ? true : false;

                for (Room room : rooms) {
                    if (room.getId().equals(classId)) {
                        room.setIsCapacityGreaterThanTwenty(isCapacityGreaterThanTwenty);
                        break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(capacityInputStream);
        }
    }

    public void saveResults() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mFitnessFile));
            int c51 = 0;
            int cUnder51 = 0;
            float average = 0;
            for (Integer f : Main.results) {
                average += f;
                if (f == 51) {
                    c51++;
                } else {
                    cUnder51++;
                }
            }

            average /= Main.results.size();

            String t0 = fixedLengthString("Average ", 15) + fixedLengthString(String.valueOf(average), 15);
            String t1 = fixedLengthString("Courses = 51 ", 15) + fixedLengthString(String.valueOf(c51), 15);
            String t2 = fixedLengthString("Courses < 51 ", 15) + fixedLengthString(String.valueOf(cUnder51), 15);
            bufferedWriter.write(t0);
            bufferedWriter.newLine();
            bufferedWriter.write(t1);
            bufferedWriter.newLine();
            bufferedWriter.write(t2);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProfFile(Schedule schedule, int[] profCourses) {
        String[] texts = new String[profCourses.length];
        for (int i = 0; i < profCourses.length; i++) {
            texts[i] = fixedLengthString(getProfessors().get(i).getProfName(), 10) + "  " + fixedLengthString(String.valueOf(profCourses[i]), 10) + " " + fixedLengthString(String.valueOf(schedule.getProfessorProgramConflict()), 27);
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mProfTextFile));
            String head = fixedLengthString("ProfName", 10) + "  " + fixedLengthString("Courses", 10) + "  " + fixedLengthString("Conflicts With Prof Program", 27);
            bufferedWriter.write(head);
            bufferedWriter.newLine();
            for (String text : texts) {
                bufferedWriter.write(text);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }

    public void saveSchedule(Configuration configuration, Schedule schedule) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFRow row;

        if (schedule.getClasses() == null) {
            return;
        }

        List<Professor> professors = configuration.getProfessors();

        String[][][] data = new String[professors.size()][6][5];
        for (int i = 0; i < data.length; i++) {
            String[][] d = new String[6][5];
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 5; k++) {
                    d[j][k] = "-";
                }
            }
            data[i] = d;
        }

        for (CourseClass courseClass : schedule.getClasses()) {
            int profId = courseClass.getProfessor().getProfId();
            data[profId][courseClass.getDay() - 1][courseClass.getTime() - 1] = courseClass.getCourse().getName() + "-" + courseClass.getRoom().getId();
        }

        for (int i = 0; i < data.length; i++) {
            Professor professor = professors.get(i);
            XSSFSheet sheet = workbook.createSheet(professor.getProfName());

            List<List<String>> lines = new ArrayList<>();

            for (int j = 0; j < 7; j++) {
                lines.add(new ArrayList<String>());
            }

            lines.get(0).add("");
            lines.get(0).add("8-10");
            lines.get(0).add("10-12");
            lines.get(0).add("13-15");
            lines.get(0).add("15-17");
            lines.get(0).add("17-19");

            lines.get(1).add("sat");
            lines.get(2).add("sun");
            lines.get(3).add("mon");
            lines.get(4).add("tues");
            lines.get(5).add("wens");
            lines.get(6).add("thurs");

            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 5; k++) {
                    lines.get(j + 1).add(data[i][j][k]);
                }
            }

            Map<String, List<String>> scheduleMap = new TreeMap<>();
            for (int j = 0; j < 7; j++) {
                scheduleMap.put("" + (j + 1), lines.get(j));
            }

            Set<String> strings = scheduleMap.keySet();
            int rowId = 0;

            for (String key : strings) {
                row = sheet.createRow(rowId++);
                List<String> stringList = scheduleMap.get(key);
                int cellId = 0;
                for (String st : stringList) {
                    Cell cell = row.createCell(cellId++);
                    cell.setCellValue(st);
                }
            }

        }

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(mResultFile);
            workbook.write(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(stream);
        }
    }

    private static void closeFileStream(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}