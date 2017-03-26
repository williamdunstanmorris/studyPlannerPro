package japrc2016;

import java.io.*;
import java.util.Calendar;


public class StudyBlock implements StudyBlockInterface, Serializable {

    private String subject;
    private Calendar startTime;
    private int duration;


    public StudyBlock(String subject, Calendar startTime, int duration) {
        this.subject = subject;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String getTopic() {
        return subject;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public Calendar getStartTime() {
        return startTime;
    }

    public void setTopic(String topic){
        this.subject = topic;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

//    //    public StudyBlock deepCopy() throws Exception{
////        ObjectOutputStream out = null;
////        ObjectInputStream in = null;
////
////        try {
////            //Serialization of the StudyBlock class. Object is written to a byte array.
////            ByteArrayOutputStream bos = new ByteArrayOutputStream();
////            out = new ObjectOutputStream(bos);
////            out.writeObject(this);
////
////            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
////            in = new ObjectInputStream(bis);
////            StudyBlock copied = (StudyBlock) in.readObject();
////            return copied;
////        } catch (Exception e){
////            System.err.println("Exception with deep copy" + e);
////            throw(e);
////        }finally {
////            out.close();
////            in.close();
////        }
////
////    }
//
//    /**
//     * Perform validation before de-serialization and serializing the object.
//     */
//

}

