/**
 * 
 */
package jp.atr.dni.bmi.desktop.neuroshareutils.nsn;

import jp.atr.dni.bmi.desktop.neuroshareutils.nsa.NSAFileInfo;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import jp.atr.dni.bmi.desktop.neuroshareutils.ConstantValues;

/**
 *
 * @author Keiji Harada [*1]</br>[*1] ATR Intl. Computational Neuroscience Labs, Decoding Group
 * @version 2011/04/22
 */
public class NSNCreateFile {

   private String outputFileName;
   private String intermediateFileNameForInfo;
   private String sMagicCode;
   private NSNFileInfo nsFileInfo;
   private ArrayList<NSNEventData> eventData = null;
   private ArrayList<NSNAnalogData> analogData = null;
   private ArrayList<NSNSegmentData> segmentData = null;
   private ArrayList<NSNNeuralEventData> neuralEventData = null;

   /**
    *
    * @param outputFileName
    */
   public NSNCreateFile(String outputFileName) {
      this.intermediateFileNameForInfo = ConstantValues.FN_HEADER + ConstantValues.FILE
              + ".fileInfo";
      this.outputFileName = outputFileName;
      this.sMagicCode = ConstantValues.MAGICCODE;
      this.nsFileInfo = new NSNFileInfo();
   }

   /**
    *
    * @param szEntityLabel
    * @return
    */
   public NSNEventData newEventData(String label) {
      // Creation if first call.
      if (this.eventData == null) {
         this.eventData = new ArrayList<NSNEventData>();
      }
      // Cut over length string.
      if (label.length() > ConstantValues.CHAR32_LENGTH) {
         String tempszEntityLabel = label.substring(0, ConstantValues.CHAR32_LENGTH);
         label = tempszEntityLabel;
      }
      // Add EventData
      this.eventData.add(new NSNEventData(this.eventData.size(), label));
      // Count up EntityCount
      this.nsFileInfo.addDwEntityCount(1);
      return this.eventData.get(this.eventData.size() - 1);
   }

   /**
    *
    * @param szEntityLabel
    * @return
    */
   public NSNAnalogData newAnalogData(String szEntityLabel) {
      // Creation if first call.
      if (this.analogData == null) {
         this.analogData = new ArrayList<NSNAnalogData>();
      }
      // Cut over length string.
      if (szEntityLabel.length() > (ConstantValues.CHAR32_LENGTH)) {
         String tempszEntityLabel = szEntityLabel.substring(0, (ConstantValues.CHAR32_LENGTH));
         szEntityLabel = tempszEntityLabel;
      }
      // Add AnalogData
      this.analogData.add(new NSNAnalogData(this.analogData.size(), szEntityLabel));
      // Count up EntityCount
      this.nsFileInfo.addDwEntityCount(1);
      return this.analogData.get(this.analogData.size() - 1);
   }

   /**
    *
    * @param szEntityLabel
    * @return
    */
   public NSNSegmentData newSegmentData(String szEntityLabel) {
      // Creation if first call.
      if (this.segmentData == null) {
         this.segmentData = new ArrayList<NSNSegmentData>();
      }
      // Cut over length string.
      if (szEntityLabel.length() > ConstantValues.CHAR32_LENGTH) {
         String tempszEntityLabel = szEntityLabel.substring(0, (ConstantValues.CHAR32_LENGTH));
         szEntityLabel = tempszEntityLabel;
      }
      // Add SegmentData
      this.segmentData.add(new NSNSegmentData(this.segmentData.size(), szEntityLabel));
      // Count up EntityCount
      this.nsFileInfo.addDwEntityCount(1);
      return this.segmentData.get(this.segmentData.size() - 1);
   }

   /**
    *
    * @param szEntityLabel
    * @return
    */
   public NSNNeuralEventData newNeuralEventData(String szEntityLabel) {
      // Creation if first call.
      if (this.neuralEventData == null) {
         this.neuralEventData = new ArrayList<NSNNeuralEventData>();
      }
      // Cut over length string.
      if (szEntityLabel.length() > ConstantValues.CHAR32_LENGTH) {
         String tempszEntityLabel = szEntityLabel.substring(0, (ConstantValues.CHAR32_LENGTH));
         szEntityLabel = tempszEntityLabel;
      }
      // Add NeuralEventData
      this.neuralEventData.add(new NSNNeuralEventData(this.neuralEventData.size(), szEntityLabel));
      // Count up EntityCount
      this.nsFileInfo.addDwEntityCount(1);
      return this.neuralEventData.get(this.neuralEventData.size() - 1);
   }

   /**
    *
    * @return
    */
   public NSAFileInfo getFileInfo() {
      return this.nsFileInfo.getMembers();
   }

   /**
    *
    * @param nsaFileInfo
    * @return
    */
   public int setFileInfo(NSAFileInfo nsaFileInfo) {
      return this.nsFileInfo.setMembers(nsaFileInfo);
   }

   /**
    *
    * @return
    */
   public int closeFile() {

      // Check the output FILE name. [.nsn] is OK. if NG, add extension and go on.
      if (!this.outputFileName.endsWith(".nsn")) {
         // Add extension.
         this.outputFileName = this.outputFileName + ".nsn";
      }

      // Delete Neuroshare FILE if it exists already.
      File outputFile = new File(this.outputFileName);
      if (outputFile.exists()) {
         outputFile.delete();
      }

      // Create all intermediate files which include ***INFO.
      // MAGICCODE, ns_FileInfo
      if (ConstantValues.NS_OK != saveFileInfo()) {
         return ConstantValues.NS_FILEERROR;
      }

      // EVENT (ns_TAGELEMENT, ns_ENTITYINFO, ns_EVENTINFO)
      if (!(this.eventData == null)) {
         for (int jj = 0; jj < this.eventData.size(); jj++) {
            if (ConstantValues.NS_OK != this.eventData.get(jj).saveEventInfo()) {
               return ConstantValues.NS_FILEERROR;
            }
         }
      }

      // ANALOG (ns_TAGELEMENT, ns_ENTITYINFO, ns_ANALOGINFO)
      if (!(this.analogData == null)) {
         for (int jj = 0; jj < this.analogData.size(); jj++) {
            if (ConstantValues.NS_OK != this.analogData.get(jj).saveAnalogInfo()) {
               return ConstantValues.NS_FILEERROR;
            }
         }
      }

      // SEGMENT (ns_TAGELEMENT, ns_ENTITYINFO, ns_SEGMENTINFO)
      if (!(this.segmentData == null)) {
         for (int jj = 0; jj < this.segmentData.size(); jj++) {
            if (ConstantValues.NS_OK != this.segmentData.get(jj).saveSegmentInfo()) {
               return ConstantValues.NS_FILEERROR;
            }

            // SEGSOURCE (ns_SEGSOURCEINFO) * num of segSourceInfo
            // if SEGSOURCE didn't exist, then skip.
            if(this.segmentData.get(jj).getSegSourceInfos() == null){
                continue;
            }
            for (int kk = 0; kk < this.segmentData.get(jj).getSegSourceInfos().size(); kk++) {
               if (ConstantValues.NS_OK != this.segmentData.get(jj).saveSegSourceInfo(kk)) {
                  return ConstantValues.NS_FILEERROR;
               }
            }
         }
      }
      // NEURALEVENT (ns_TAGELEMENT, ns_ENTITYINFO, ns_NEURALINFO)
      if (!(this.neuralEventData == null)) {
         for (int jj = 0; jj < this.neuralEventData.size(); jj++) {
            if (ConstantValues.NS_OK != this.neuralEventData.get(jj).saveNeuralInfo()) {
               return ConstantValues.NS_FILEERROR;
            }
         }
      }

      // Integrate all intermediate files. (Create NSNfile.)
      // FILEINFO-EVENT-NEURALEVENT-ANALOG-SEGMENT.
      // Then, delete all intermediate files.

      // MAGICCODE, ns_FileInfo
      if (ConstantValues.NS_OK != FileCat(this.outputFileName, this.intermediateFileNameForInfo)) {
         return ConstantValues.NS_FILEERROR;
      }

      // Delete the intermediate FILE. (MAGICCODE, ns_FILEINFO)
      if (!(new File(this.intermediateFileNameForInfo)).delete()) {
         return ConstantValues.NS_FILEERROR;
      }

      // EVENT

      if (!(this.eventData == null)) {
         for (int jj = 0; jj < this.eventData.size(); jj++) {
            // Integrate the intermediate FILE.(INFO : ns_TAGELEMENT, ns_ENTITYINFO, ns_EVENTINFO)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.eventData.get(jj)).getIntermediateFileNameForInfo())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.eventData.get(jj).getIntermediateFileNameForInfo()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Check existence of the intermediate FILE.
            File tf = new File(this.eventData.get(jj).getIntermediateFileNameForData());
            if (!tf.exists()) {
               continue;
            }
            // Integrate the intermediate FILE. (DATA : EventData)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.eventData.get(jj)).getIntermediateFileNameForData())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.eventData.get(jj).getIntermediateFileNameForData()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
         }
      }

      // NEURALEVENT

      if (!(this.neuralEventData == null)) {
         for (int jj = 0; jj < this.neuralEventData.size(); jj++) {
            // Integrate the intermediate FILE.(INFO : ns_TAGELEMENT, ns_ENTITYINFO, ns_NEURALINFO)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.neuralEventData.get(jj)).getIntermediateFileNameForInfo())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.neuralEventData.get(jj).getIntermediateFileNameForInfo()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Check existence of the intermediate FILE.
            File tf = new File(this.neuralEventData.get(jj).getIntermediateFileNameForData());
            if (!tf.exists()) {
               continue;
            }
            // Integrate the intermediate FILE. (DATA : EventData)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.neuralEventData.get(jj)).getIntermediateFileNameForData())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.neuralEventData.get(jj).getIntermediateFileNameForData()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
         }
      }
      // ANALOG
      if (!(this.analogData == null)) {
         for (int jj = 0; jj < this.analogData.size(); jj++) {

            // Integrate the intermediate FILE. (INFO : ns_TAGELEMENT, ns_ENTITYINFO, ns_ANALOGINFO)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    this.analogData.get(jj).getIntermediateFileNameForInfo())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.analogData.get(jj).getIntermediateFileNameForInfo()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Check existence of the intermediate FILE.
            File tf = new File(this.analogData.get(jj).getIntermediateFileNameForData());
            if (!tf.exists()) {
               continue;
            }
            // Integrate the intermediate FILE. (DATA : AnalogData)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.analogData.get(jj)).getIntermediateFileNameForData())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.analogData.get(jj).getIntermediateFileNameForData()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }

         }
      }

      // SEGMENT
      if (!(this.segmentData == null)) {
         for (int jj = 0; jj < this.segmentData.size(); jj++) {

            // Integrate the intermediate FILE. (INFO : ns_TAGELEMENT, ns_ENTITYINFO,
            // ns_SEGMENTINFO)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.segmentData.get(jj)).getIntermediateFileNameForInfo())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.segmentData.get(jj).getIntermediateFileNameForInfo()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Check existence of the intermediate FILE.
            File sf = new File(this.segmentData.get(jj).getIntermediateFileNameForSourceInfo());
            if (!sf.exists()) {
               continue;
            }
            // Integrate the intermediate FILE. (INFO : ns_TAGELEMENT, ns_ENTITYINFO,
            // ns_SEGMENTINFO)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.segmentData.get(jj)).getIntermediateFileNameForSourceInfo())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.segmentData.get(jj).getIntermediateFileNameForSourceInfo()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Check existence of the intermediate FILE.
            File tf = new File(this.segmentData.get(jj).getIntermediateFileNameForData());
            if (!tf.exists()) {
               continue;
            }
            // Integrate the intermediate FILE. (DATA : AnalogData)
            if (ConstantValues.NS_OK != FileCat(this.outputFileName,
                    (this.segmentData.get(jj)).getIntermediateFileNameForData())) {
               return ConstantValues.NS_FILEERROR;
            }
            // Delete the intermediate FILE.
            if (!(new File(this.segmentData.get(jj).getIntermediateFileNameForData()).delete())) {
               return ConstantValues.NS_FILEERROR;
            }

         }
      }
      // then
      return ConstantValues.NS_OK;
   }

   private int FileCat(String NeuroshareFilePath, String intermediateFilePath) {
      // Integrate two files.

      int rtnVal = ConstantValues.NS_OK;
      File fDst = new File(NeuroshareFilePath);
      File fSrc = new File(intermediateFilePath);
      FileOutputStream fosDst = null;
      FileInputStream fisSrc = null;

      try {
         fosDst = new FileOutputStream(fDst, true);
         fisSrc = new FileInputStream(fSrc);
         byte[] buffer = new byte[1000 * 1024]; // 1MB for buffer.
         int len = 0;

         while ((len = fisSrc.read(buffer, 0, buffer.length)) != -1) {
            fosDst.write(buffer, 0, len);
         }
         fosDst.flush();

         fosDst.close();
         fisSrc.close();

         // Then, NS_OK.
         rtnVal = ConstantValues.NS_OK;

      } catch (FileNotFoundException e) {
         // File Not Found.
         e.printStackTrace();

         // Then, NS_FILEERROR.
         rtnVal = ConstantValues.NS_FILEERROR;

      } catch (IOException e) {
         // File I/O error.
         e.printStackTrace();

         // Then, NS_FILEERROR.
         rtnVal = ConstantValues.NS_FILEERROR;

      } finally {

         try {
            if (!fosDst.equals(null)) {
               fosDst.close();
            }
            if (!fisSrc.equals(null)) {
               fisSrc.close();
            }

         } catch (IOException e) {
            // May be sequence doesn't reach here.
            e.printStackTrace();
            rtnVal = ConstantValues.NS_FILEERROR;

         }

      }

      // return the value.
      return rtnVal;
   }

   private int saveFileInfo() {

      int rtnVal = ConstantValues.NS_OK;
      File tempFile = null;
      FileOutputStream fos = null;
      DataOutputStream dos = null;

      try {
         // Open the intermediatefile. (use FileOutputStream, DataOutputStream.)
         tempFile = new File(this.intermediateFileNameForInfo);
         fos = new FileOutputStream(tempFile, true);
         dos = new DataOutputStream(fos);

         // Add ns_FileInfo.
         // Write in BIG Endian (JAVA Default)
         /*
          * dos.writeBytes(this.MAGICCODE); dos.writeByte(0x00); String szFileType =
          * (this.nsFileInfo.getMembers().getSzFileType() + (new Const_values()).getBlank32())
          * .substring(0, Const_values.CHAR32_LENGTH); dos.writeBytes(szFileType);
          * dos.writeInt(this.nsFileInfo.getDwEntityCount());
          * dos.writeDouble(this.nsFileInfo.getMembers().getDTimeStampResolution());
          * dos.writeDouble(this.nsFileInfo.getMembers().getDTimeSpan()); String szAppName =
          * (this.nsFileInfo.getMembers().getSzAppName() + (new Const_values()).getBlank64())
          * .substring(0, (new Const_values()).getChar64()); dos.writeBytes(szAppName);
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_Year());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_Month());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_DayOfWeek());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_Day());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_Hour());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_Min());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_Sec());
          * dos.writeInt(this.nsFileInfo.getMembers().getDwTime_MilliSec()); String szFileComment =
          * (this.nsFileInfo.getMembers().getSzFileComment() + (new Const_values())
          * .getBlank256()).substring(0, (new Const_values()).getChar256());
          * dos.writeBytes(szFileComment);
          */

         // Write in LITTLE Endian (MATLAB Default)
         NSAFileInfo nsaFileInfo = nsFileInfo.getMembers();

         dos.writeBytes(this.sMagicCode);
         dos.writeByte(0x00);
         String szFileType = (nsaFileInfo.getSzFileType() + ConstantValues.BLANK_CHAR32).substring(0, ConstantValues.CHAR32_LENGTH);
         dos.writeBytes(szFileType);
         dos.writeInt(Integer.reverseBytes(this.nsFileInfo.getDwEntityCount()));
         dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(nsaFileInfo.getDTimeStampResolution())));
         dos.writeLong(Long.reverseBytes(Double.doubleToLongBits(nsaFileInfo.getDTimeSpan())));
         String szAppName = (nsaFileInfo.getSzAppName() + ConstantValues.BLANK_CHAR64).substring(0, ConstantValues.CHAR64_LENGTH);
         dos.writeBytes(szAppName);
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_Year()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_Month()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_DayOfWeek()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_Day()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_Hour()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_Min()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_Sec()));
         dos.writeInt(Integer.reverseBytes(nsaFileInfo.getDwTime_MilliSec()));
         String szFileComment = (nsaFileInfo.getSzFileComment() + ConstantValues.BLANK_CHAR256).substring(0, ConstantValues.CHAR256_LENGTH);
         dos.writeBytes(szFileComment);

         dos.close();
         fos.close();

         // Then, NS_OK.
         rtnVal = ConstantValues.NS_OK;

      } catch (FileNotFoundException e) {
         // File Not Found.
         e.printStackTrace();

         // Then, NS_FILEERROR.
         rtnVal = ConstantValues.NS_FILEERROR;

      } catch (IOException e) {
         // File I/O error.
         e.printStackTrace();

         // Then, NS_FILEERROR.
         rtnVal = ConstantValues.NS_FILEERROR;

      } finally {
         try {
            if (!dos.equals(null)) {
               dos.close();
            }
            if (!fos.equals(null)) {
               fos.close();
            }

         } catch (IOException e) {
            // May be sequence doesn't reach here.
            e.printStackTrace();
            rtnVal = ConstantValues.NS_FILEERROR;

         }
      }

      // return the value.
      return rtnVal;
   }
}
