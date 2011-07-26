package jp.atr.dni.bmi.desktop.model.api.data;

import java.util.List;

/**
 *
 * @author Makoto Takemiya - [武宮 誠] <br />
 * <a href="http://www.atr.jp">ATR - [株式会社・国際電氣通信基礎技術研究所]</a>
 *
 * @version 2011/07/15
 */
interface APIDataProvider<T> {

   /**
    * 
    * @return the size of the data set
    */
   public int size();

   /**
    * 
    * @param from - index to start reading data from
    * @param count - index to start reading data to
    * @return the data from the <code>from</code> index to the <code>to</code> 
    * index, exclusive. If <code>to</code> is greater than the size, only data 
    * up to the end of the data set will be returned, without any exception 
    * being thrown
    */
   public List<T> getData(int from, int to);
}
