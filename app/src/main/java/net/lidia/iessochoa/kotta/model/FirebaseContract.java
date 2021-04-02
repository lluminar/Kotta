package net.lidia.iessochoa.kotta.model;

public class FirebaseContract {
    public static class PartituraEntry{
        public static final String COLLECTION_NAME="partituras";
        public static final String USER="user";
        public static final String CREATION_DATE="creationDate";
        public static final String NAME="name";
        public static final String AUTHOR="author";
        public static final String INSTRUMENT="instrument";
        public static final String CATEGORY="category";
        public static final String PDF="pdf";

        public static final String STORAGE_PATH_UPLOADS = "uploads/";
        public static final String DATABASE_PATH_UPLOADS = "uploads";
    }
}
