/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.workspace;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.event.ChangeListener;
import jp.atr.dni.bmi.desktop.model.api.AnalogChannel;
import jp.atr.dni.bmi.desktop.model.api.Channel;
import jp.atr.dni.bmi.desktop.model.api.ChannelType;
import jp.atr.dni.bmi.desktop.model.api.EventChannel;
import jp.atr.dni.bmi.desktop.model.api.NeuralSpikeChannel;
import jp.atr.dni.bmi.desktop.model.api.SegmentChannel;
import jp.atr.dni.bmi.desktop.model.api.data.APIList;
import jp.atr.dni.bmi.desktop.model.api.data.NSNAnalogData;
import jp.atr.dni.bmi.desktop.model.api.data.NSNEvent;
import jp.atr.dni.bmi.desktop.model.api.data.NSNEventData;
import jp.atr.dni.bmi.desktop.model.api.data.NSNNeuralSpikeData;
import jp.atr.dni.bmi.desktop.model.api.data.NSNSegmentData;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

public class CreateNewFileWizardPanel2 implements WizardDescriptor.ValidatingPanel {

    private ArrayList<Channel> channels = new ArrayList<Channel>();
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CreateNewFileVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new CreateNewFileVisualPanel2();
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }
    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {

        // Set showed tab.
        Object index = ((WizardDescriptor) settings).getProperty("fileFormat");
        Integer indexValue = (Integer) index;
        component.setShowedTabIndex(indexValue);

        // Get channels.
        channels = (ArrayList<Channel>) ((WizardDescriptor) settings).getProperty("selectedChannels");

        Double a = calucurateTimestampResolution();
        component.setCalcuratedTimestampResolution(a);

        Double b = calucurateTimeSpan();
        component.setCalcuratedTimeSpan(b);
    }

    public void storeSettings(Object settings) {

        try {
            validate();
            ((WizardDescriptor) settings).putProperty("fileType", ((CreateNewFileVisualPanel2) getComponent()).getNeuroshareFileType());
            ((WizardDescriptor) settings).putProperty("timeStampResolution", Double.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareTimeStampResolution()));
            ((WizardDescriptor) settings).putProperty("timeSpan", Double.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareTimeSpan()));
            ((WizardDescriptor) settings).putProperty("applicationName", "ATR BrainLiner v0.9");
            ((WizardDescriptor) settings).putProperty("year", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareYear()));
            ((WizardDescriptor) settings).putProperty("month", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareMonth()));
            ((WizardDescriptor) settings).putProperty("day", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareDay()));
            ((WizardDescriptor) settings).putProperty("hour", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareHour()));
            ((WizardDescriptor) settings).putProperty("min", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareMin()));
            ((WizardDescriptor) settings).putProperty("sec", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareSec()));
            ((WizardDescriptor) settings).putProperty("milliSec", Integer.valueOf(((CreateNewFileVisualPanel2) getComponent()).getNeuroshareMilliSec()));
            ((WizardDescriptor) settings).putProperty("comments", ((CreateNewFileVisualPanel2) getComponent()).getNeuroshareComments());

        } catch (WizardValidationException ex) {
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        String buffer = "";

        try {
            buffer = component.getNeuroshareTimeStampResolution();
            Double v = Double.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid TimeStampResolution value", null);
        }

        try {
            buffer = component.getNeuroshareTimeSpan();
            Double v = Double.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid TimeSpan value", null);
        }

        try {
            buffer = component.getNeuroshareYear();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid Year value", null);
        }

        try {
            buffer = component.getNeuroshareMonth();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid Month value", null);
        }

        try {
            buffer = component.getNeuroshareDay();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid Day value", null);
        }

        try {
            buffer = component.getNeuroshareHour();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid Hour value", null);
        }

        try {
            buffer = component.getNeuroshareMin();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid Min value", null);
        }

        try {
            buffer = component.getNeuroshareSec();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid Sec value", null);
        }

        try {
            buffer = component.getNeuroshareMilliSec();
            Integer v = Integer.valueOf(buffer);
        } catch (NumberFormatException e) {
            throw new WizardValidationException(null, "Invalid MilliSec value", null);
        }

    }

    private Double calucurateTimestampResolution() {
        double timestampResolution = Double.MAX_VALUE;
        double tempValue = 0;
        boolean analogOrSegmentEntityExistFlag = false;
        int size = this.channels.size();

        // Search minimum timestamp resolution from all channels.
        for (int i = 0; i < size; i++) {
            Channel ch = this.channels.get(i);

            // Only ANALOG & SEGMENT have timestamp resolution( 1/samplingRate ).
            if (ch.getType() == ChannelType.ANALOG) {
                analogOrSegmentEntityExistFlag = true;
                AnalogChannel ac = (AnalogChannel) ch;

                tempValue = 1 / ac.getSamplingRate();

                if (tempValue < timestampResolution) {
                    timestampResolution = tempValue;
                }
            } else if (ch.getType() == ChannelType.SEGMENT) {
                analogOrSegmentEntityExistFlag = true;
                SegmentChannel sc = (SegmentChannel) ch;

                tempValue = 1 / sc.getSamplingRate();

                if (tempValue < timestampResolution) {
                    timestampResolution = tempValue;
                }
            }
        }

        if (!analogOrSegmentEntityExistFlag) {
            // Case : Analog or SegmentEntity doesn't exist.
            timestampResolution = 0;
        }

        return timestampResolution;
    }

    private Double calucurateTimeSpan() {

        // Calcurate TimeSpan which means elapsed time.
        double timeSpan = 0;
        double tempValue = 0;
        int size = this.channels.size();

        // Search longest timespan from all channels.
        for (int i = 0; i < size; i++) {
            Channel ch = this.channels.get(i);

            if (ch.getType() == ChannelType.ANALOG) {

                // Analog Channel
                AnalogChannel ac = (AnalogChannel) ch;
                NSNAnalogData ad = ac.getData();
                ArrayList<Double> timeStamps = ad.getTimeStamps();
                int rowSize = timeStamps.size();
                Double lastRowStartTime = timeStamps.get(rowSize - 1);
                int dataCountInLastRow = ad.getValues().get(rowSize - 1).size();

                // timespan is lastRowStartTime + samplingRate * (dataCountInLastRow -1);
                tempValue = lastRowStartTime + ac.getSamplingRate() * (dataCountInLastRow - 1);

                if (tempValue > timeSpan) {
                    timeSpan = tempValue;
                }
            } else if (ch.getType() == ChannelType.SEGMENT) {

                // Segment Channel
                SegmentChannel sc = (SegmentChannel) ch;
                NSNSegmentData sd = sc.getData();
                ArrayList<Double> timeStamps = sd.getTimeStamps();
                int rowSize = timeStamps.size();
                Double lastRowStartTime = timeStamps.get(rowSize - 1);
                int dataCountInLastRow = sd.getValues().get(rowSize - 1).size();

                // timespan is lastRowStartTime + samplingRate * (dataCountInLastRow -1);
                tempValue = lastRowStartTime + sc.getSamplingRate() * (dataCountInLastRow - 1);

                if (tempValue > timeSpan) {
                    timeSpan = tempValue;
                }
            } else if (ch.getType() == ChannelType.EVENT) {

                // Event Channel
                EventChannel ec = (EventChannel) ch;
                NSNEventData ed = ec.getData();
                APIList<NSNEvent> events = ed.getEvents();
                NSNEvent lastEvent = events.get(events.size() - 1);

                // timespan is lastTimestamp
                tempValue = lastEvent.getTimestamp();

                if (tempValue > timeSpan) {
                    timeSpan = tempValue;
                }
            } else if (ch.getType() == ChannelType.NEURAL_SPIKE) {

                // NeuralEvent Channel
                NeuralSpikeChannel nc = (NeuralSpikeChannel) ch;
                NSNNeuralSpikeData nd = nc.getData();
                APIList<Double> timeStamps = nd.getTimeStamps();
                Double lastValue = timeStamps.get(timeStamps.size() - 1);

                // timespan is lastTimestamp
                tempValue = lastValue;

                if (tempValue > timeSpan) {
                    timeSpan = tempValue;
                }
            }

        }

        return timeSpan;
    }
}
