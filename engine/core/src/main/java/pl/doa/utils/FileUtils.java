/*
 * Copyright 2001-2007 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileUtils.java 3884 2007-08-22 08:52:24Z gbevin $
 */
package pl.doa.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class FileUtils {

    public static void moveFile(File source, File target)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");
        if (null == target)
            throw new IllegalArgumentException("target can't be null.");

        if (!source.exists()) {
            throw new FileUtilsErrorException("The source file '"
                    + source.getAbsolutePath() + "' does not exist.");
        }

        // copy
        copy(source, target);

        // then delete sourcefile
        deleteFile(source);
    }

    public static void moveDirectory(File source, File target)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");
        if (null == target)
            throw new IllegalArgumentException("target can't be null.");

        if (!source.exists()) {
            throw new FileUtilsErrorException("The source directory '"
                    + source.getAbsolutePath() + "' does not exist.");
        }

        // Create target if it does not exist already
        if (!target.exists()) {
            target.mkdirs();
        }

        String[] filelist = source.list();

        for (String filelist_element : filelist) {
            File current_file =
                    new File(source.getAbsolutePath() + File.separator
                            + filelist_element);
            File target_file =
                    new File(target.getAbsolutePath() + File.separator
                            + filelist_element);

            if (current_file.isDirectory()) {
                moveDirectory(current_file, target_file);
            } else {
                moveFile(current_file, target_file);
            }
        }

        // If we get here it means we're finished with this directory ... delete it.
        deleteFile(source);

    }

    public static void deleteDirectory(File source)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");

        if (!source.exists()) {
            throw new FileUtilsErrorException("The directory '"
                    + source.getAbsolutePath() + "' does not exist");
        }

        String[] filelist = source.list();

        for (String filelist_element : filelist) {
            File current_file =
                    new File(source.getAbsolutePath() + File.separator
                            + filelist_element);

            if (current_file.isDirectory()) {
                deleteDirectory(current_file);
            } else {
                deleteFile(current_file);
            }
        }

        // If we get here it means we're finished with this directory ... delete it.
        deleteFile(source);
    }

    public static void copy(InputStream inputStream, OutputStream outputStream)
            throws FileUtilsErrorException {
        if (null == inputStream)
            throw new IllegalArgumentException("inputStream can't be null.");
        if (null == outputStream)
            throw new IllegalArgumentException("outputStream can't be null.");

        try {
            byte[] buffer = new byte[1024];
            int return_value = -1;

            return_value = inputStream.read(buffer);

            while (-1 != return_value) {
                outputStream.write(buffer, 0, return_value);
                return_value = inputStream.read(buffer);
            }
        } catch (IOException e) {
            throw new FileUtilsErrorException(
                    "Error during the copying of streams.", e);
        }
    }

    public static File copy(InputStream inputStream, File target)
            throws FileUtilsErrorException {
        if (null == inputStream)
            throw new IllegalArgumentException("inputStream can't be null.");
        if (null == target)
            throw new IllegalArgumentException("target can't be null.");

        try {
            FileOutputStream file_output_stream = new FileOutputStream(target);

            copy(inputStream, file_output_stream);

            file_output_stream.close();
        } catch (IOException e) {
            throw new FileUtilsErrorException(
                    "Error while copying an input stream to file '"
                            + target.getAbsolutePath() + "'.", e);
        }
        return target;
    }

    public static void copy(File source, OutputStream outputStream)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");
        if (null == outputStream)
            throw new IllegalArgumentException("outputStream can't be null.");

        try {
            FileInputStream file_input_stream = new FileInputStream(source);

            copy(file_input_stream, outputStream);

            file_input_stream.close();
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while copying file '"
                    + source.getAbsolutePath() + "' to an output stream.", e);
        }
    }

    public static void copy(File source, File target)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");
        if (null == target)
            throw new IllegalArgumentException("target can't be null.");

        try {
            FileInputStream file_input_stream = new FileInputStream(source);
            FileOutputStream file_output_stream = new FileOutputStream(target);

            copy(file_input_stream, file_output_stream);

            file_output_stream.close();
            file_input_stream.close();
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while copying file '"
                    + source.getAbsolutePath() + "' to file '"
                    + target.getAbsolutePath() + "'.", e);
        }
    }

    public static ByteArrayOutputStream readStream(InputStream inputStream)
            throws FileUtilsErrorException {
        if (null == inputStream)
            throw new IllegalArgumentException("inputStream can't be null.");

        try {
            byte[] buffer = new byte[1024];
            int return_value = -1;
            ByteArrayOutputStream output_stream =
                    new ByteArrayOutputStream(buffer.length);

            return_value = inputStream.read(buffer);

            while (-1 != return_value) {
                output_stream.write(buffer, 0, return_value);
                return_value = inputStream.read(buffer);
            }

            output_stream.close();

            inputStream.close();

            return output_stream;
        } catch (IOException e) {
            throw new FileUtilsErrorException(
                    "Error while reading the complete contents of an input stream.",
                    e);
        }
    }

    public static String readString(InputStream inputStream)
            throws FileUtilsErrorException {
        if (null == inputStream)
            throw new IllegalArgumentException("inputStream can't be null.");

        return readStream(inputStream).toString();
    }

    public static String readString(Reader reader)
            throws FileUtilsErrorException {
        if (null == reader)
            throw new IllegalArgumentException("reader can't be null.");

        try {
            char[] buffer = new char[1024];
            StringBuilder result = new StringBuilder();

            int size = reader.read(buffer);
            while (size != -1) {
                result.append(buffer, 0, size);
                size = reader.read(buffer);
            }

            return result.toString();
        } catch (IOException e) {
            throw new FileUtilsErrorException(
                    "Error while reading the complete contents of an reader.",
                    e);
        }
    }

    public static String readString(InputStream inputStream, String encoding)
            throws FileUtilsErrorException {
        if (null == inputStream)
            throw new IllegalArgumentException("inputStream can't be null.");

        try {
            return readStream(inputStream).toString(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new FileUtilsErrorException("Encoding '" + encoding
                    + "' is not supported.", e);
        }
    }

    public static byte[] readBytes(InputStream inputStream)
            throws FileUtilsErrorException {
        if (null == inputStream)
            throw new IllegalArgumentException("inputStream can't be null.");

        return readStream(inputStream).toByteArray();
    }

    public static String readString(URL source) throws FileUtilsErrorException {
        return readString(source, null);
    }

    public static String readString(URL source, String encoding)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");

        try {
            URLConnection connection = source.openConnection();
            connection.setUseCaches(false);
            InputStream input_stream = connection.getInputStream();
            String content = null;
            if (null == encoding) {
                content = readString(input_stream);
            } else {
                content = readString(input_stream, encoding);
            }
            input_stream.close();
            return content;
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while reading url '"
                    + source.toString() + ".", e);
        }
    }

    public static byte[] readBytes(URL source) throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");

        try {
            URLConnection connection = source.openConnection();
            connection.setUseCaches(false);
            InputStream input_stream = connection.getInputStream();
            byte[] content = readBytes(input_stream);
            input_stream.close();
            return content;
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while reading url '"
                    + source.toString() + ".", e);
        }
    }

    public static String readString(File source) throws FileUtilsErrorException {
        return readString(source, null);
    }

    public static String readString(File source, String encoding)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");

        try {
            FileInputStream file_input_stream = new FileInputStream(source);
            String content = null;
            if (null == encoding) {
                content = readString(file_input_stream);
            } else {
                content = readString(file_input_stream, encoding);
            }
            file_input_stream.close();
            return content;
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while reading url '"
                    + source.getAbsolutePath() + ".", e);
        }
    }

    public static byte[] readBytes(File source) throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");

        try {
            FileInputStream file_input_stream = new FileInputStream(source);
            byte[] content = readBytes(file_input_stream);
            file_input_stream.close();
            return content;
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while reading file '"
                    + source.getAbsolutePath() + ".", e);
        }
    }

    public static void writeBytes(byte[] content, File destination)
            throws FileUtilsErrorException {
        if (null == content)
            throw new IllegalArgumentException("content can't be null.");
        if (null == destination)
            throw new IllegalArgumentException("destination can't be null.");

        try {
            FileOutputStream file_output_stream =
                    new FileOutputStream(destination);
            file_output_stream.write(content);
            file_output_stream.flush();
            file_output_stream.close();
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while write a string to '"
                    + destination.getAbsolutePath() + ".", e);
        }
    }

    public static void writeString(String content, File destination)
            throws FileUtilsErrorException {
        if (null == content)
            throw new IllegalArgumentException("content can't be null.");
        if (null == destination)
            throw new IllegalArgumentException("destination can't be null.");

        try {
            FileWriter file_writer = null;

            file_writer = new FileWriter(destination);

            file_writer.write(content, 0, content.length());
            file_writer.flush();
            file_writer.close();
        } catch (IOException e) {
            throw new FileUtilsErrorException("Error while write a string to '"
                    + destination.getAbsolutePath() + ".", e);
        }
    }

    public static void deleteFile(File file) {
        if (null == file)
            throw new IllegalArgumentException("file can't be null.");

        file.delete();
    }

    public static String getUniqueFilename() {
        Date current_date = new Date();

        return current_date.getTime() + "-" + (long) (1000000 * Math.random());
    }

    public static void unzipFile(File source, File destination)
            throws FileUtilsErrorException {
        if (null == source)
            throw new IllegalArgumentException("source can't be null.");
        if (null == destination)
            throw new IllegalArgumentException("destination can't be null.");

        ZipFile zip_file = null;
        Enumeration entries = null;

        try {
            zip_file = new ZipFile(source);
        } catch (IOException e) {
            throw new FileUtilsErrorException(
                    "Error while creating the zipfile '"
                            + source.getAbsolutePath() + "'.", e);
        }
        entries = zip_file.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = null;
            InputStream input_stream = null;
            String output_filename = null;
            File output_file = null;
            StringBuilder output_file_directoryname = null;
            File output_file_directory = null;
            FileOutputStream file_output_stream = null;
            byte[] buffer = new byte[1024];
            int return_value = -1;

            entry = (ZipEntry) entries.nextElement();
            try {
                input_stream = zip_file.getInputStream(entry);
            } catch (IOException e) {
                throw new FileUtilsErrorException(
                        "Error while obtaining the inputstream for entry '"
                                + entry.getName() + "'.", e);
            }

            output_filename =
                    destination.getAbsolutePath() + File.separator
                            + entry.getName().replace('/', File.separatorChar);
            output_file = new File(output_filename);
            output_file_directoryname =
                    new StringBuilder(output_file.getPath());
            output_file_directoryname.setLength(output_file_directoryname
                    .length()
                    - output_file.getName().length()
                    - File.separator.length());
            output_file_directory =
                    new File(output_file_directoryname.toString());
            if (!output_file_directory.exists()) {
                if (!output_file_directory.mkdirs()) {
                    throw new FileUtilsErrorException(
                            "Couldn't create directory '"
                                    + output_file_directory.getAbsolutePath()
                                    + "' and its parents.");
                }
            } else {
                if (!output_file_directory.isDirectory()) {
                    throw new FileUtilsErrorException("Destination '"
                            + output_file_directory.getAbsolutePath()
                            + "' exists and is not a directory.");
                }
            }

            try {
                file_output_stream = new FileOutputStream(output_filename);
            } catch (IOException e) {
                throw new FileUtilsErrorException(
                        "Error while creating the output stream for file '"
                                + output_filename + "'.", e);
            }

            try {
                return_value = input_stream.read(buffer);

                while (-1 != return_value) {
                    file_output_stream.write(buffer, 0, return_value);
                    return_value = input_stream.read(buffer);
                }
            } catch (IOException e) {
                throw new FileUtilsErrorException(
                        "Error while uncompressing entry '" + output_filename
                                + "'.", e);
            }

            try {
                file_output_stream.close();
            } catch (IOException e) {
                throw new FileUtilsErrorException(
                        "Error while closing the output stream for file '"
                                + output_filename + "'.", e);
            }
            try {
                input_stream.close();
            } catch (IOException e) {
                throw new FileUtilsErrorException(
                        "Error while closing the input stream for entry '"
                                + entry.getName() + "'.", e);
            }
        }

        try {
            zip_file.close();
        } catch (IOException e) {
            throw new FileUtilsErrorException(
                    "Error while closing the zipfile '"
                            + source.getAbsolutePath() + "'.", e);
        }
    }

    public static String getBaseName(File file) {
        return getBaseName(file.getName());
    }

    public static String getBaseName(String fileName) {
        if (null == fileName)
            throw new IllegalArgumentException("fileName can't be null.");

        String basename = null;

        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            basename = fileName.substring(0, index);
        }

        return basename;
    }

    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        if (null == fileName)
            throw new IllegalArgumentException("fileName can't be null.");

        String ext = null;

        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            ext = fileName.substring(index + 1).toLowerCase();
        }

        return ext;
    }
}
