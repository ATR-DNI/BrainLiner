package jp.atr.dni.bmi.desktop.model.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author makoto
 */
@ServiceProvider(service = Workspace.class)
public final class Workspace {

   private List<Channel> channels = Collections.synchronizedList(new LinkedList<Channel>());
   private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

   public void addChannel(Channel channel) {
      channels.add(channel);
      fire("ChannelAdd", null, channel);
   }

   public void addChannels(Channel[] toAdd) {
      for (Channel c : toAdd) {
         channels.add(c);
      }
      fire("ChannelsAdd", null, toAdd);
   }

   public void addChannels(Collection<Channel> toAdd) {
      channels.addAll(toAdd);
      fire("ChannelsAdd", null, toAdd);
   }

   public void removeChannel(Channel channel) {
      channels.remove(channel);
      fire("ChannelRemove", channel, null);
   }

   public void removeChannels(Collection<Channel> toRemove) {
      channels.removeAll(channels);
      fire("ChannelsRemove", toRemove, null);
   }

   public void removeChannels(Channel[] toRemove) {
      for (Channel c : toRemove) {
         channels.remove(c);
      }
      fire("ChannelsRemove", toRemove, null);
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   public Channel getChannel(int ndx) {
      return channels.get(ndx);
   }

   public int numChannels() {
      return channels.size();
   }

   // Fire the property change event. (All listeners receive this event.) [in your propertyChange method]
   private void fire(String propertyName, Object oldVal, Object newVal) {
      synchronized (listeners) {
         for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(new PropertyChangeEvent("Workspace.firePropertyChange", propertyName, oldVal, newVal));
         }
      }
   }

   public void updateChannelHeader(Channel oldChannel, Channel channel) {

      // If ChannelID, DisplayName, ChannelType and SourceFilePath is same, then update.
      for (int i = 0; i < channels.size(); i++) {
         Channel ch = channels.get(i);
         if (ch.getId() == oldChannel.getId()) {
            if (ch.getLabel().equals(oldChannel.getLabel())) {
               if (ch.getType().equals(oldChannel.getType())) {
                  if (ch.getURI().equals(oldChannel.getURI())) {
                     channels.set(i, channel);
                     fire("UpdateChannel", oldChannel, channel);
                     return;
                  }
               }
            }
         }
      }
   }
}
