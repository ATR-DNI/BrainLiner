/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.model.api.data;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author makoto
 */
public final class ChannelDataProvider<T> implements APIDataProvider<T> {

   @Override
   public int size() {
      return 0;
   }

   @Override
   public List<T> getData(int from, int to) {
      return null; //TODO:
   }
}