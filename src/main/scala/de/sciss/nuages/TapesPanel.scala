/*
 *  TapesFrame.scala
 *  (NuagesPompe)
 *
 *  Copyright (c) 2010-2011 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either
 *  version 2, june 1991 of the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License (gpl.txt) along with this software; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 *
 *
 *  Changelog:
 */

package de.sciss.nuages

import javax.swing._
import java.awt.event._
import collection.breakOut
import collection.immutable.{IndexedSeq => IIdxSeq}
import table._
import java.text.SimpleDateFormat
import java.util.{Locale, Date, Comparator}
import java.awt._
import java.io.File
import javax.swing.event.{ListSelectionListener, ListSelectionEvent}
import de.sciss.synth.Model
import de.sciss.synth.io.{AudioFile, AudioFileSpec}

object TapesPanel {
   case class SelectionChanged( selection: TapeInfo* )

   private case class Column( idx: Int, name: String, minWidth: Int, maxWidth: Int, extract: TapeInfo => Any,
                              renderer: Option[ TableCellRenderer ], sorter: Option[ Comparator[ _ ]])
//   private val UnknownColumn = Column( -1, null, 0, 0, null, None, None )

   private object IntComparator extends Comparator[ Int ] {
      def compare( a: Int, b: Int ) = a.compare( b )
   }
//   private object LongComparator extends Comparator[ Long ] {
//      def compare( a: Long, b: Long ) = a.compare( b )
//   }
   private object DoubleComparator extends Comparator[ Double ] {
      def compare( a: Double, b: Double ) = a.compare( b )
   }
   private object DateComparator extends Comparator[ Date ] {
      def compare( a: Date, b: Date ) = a.compareTo( b )
   }

   private object RightAlignedRenderer extends DefaultTableCellRenderer {
      setHorizontalAlignment( SwingConstants.TRAILING )
   }
   private object DateRenderer extends DefaultTableCellRenderer {
      private val df = new SimpleDateFormat( "yy-MMM-dd", Locale.US )
      override def setValue( value: AnyRef ) {
         super.setValue( if( value == null ) null else df.format( value ))
      }
   }
   private object DurationRenderer extends DefaultTableCellRenderer {
      override def setValue( value: AnyRef ) {
         super.setValue( if( value == null ) null else value match {
            case d: java.lang.Double => {
               val secsP   = (d.doubleValue() + 0.5).toInt
               val mins    = secsP / 60
               val secs    = secsP % 60
               (mins + 100).toString.substring( 1 ) + ":" +
               (secs + 100).toString.substring( 1 ) 
            }
            case _ => value
         })
      }
   }

   case class TapeInfo( file: File, spec: AudioFileSpec )

   def fromFolder( folder: File, title: String = "Tapes", sizeVariant: String = "small" ) = {
      val files = folder.listFiles()
      val infos: IIdxSeq[ TapeInfo ] = if( files != null ) files.collect({
         case x if( x.isFile && (try { AudioFile.identify( x ).isDefined } catch { case e => false })) =>
            TapeInfo( x, AudioFile.readSpec( x ))
      })( breakOut ) else IIdxSeq.empty
      new TapesPanel( infos, title, sizeVariant )
   }
}

class TapesPanel( infos: IIdxSeq[ TapesPanel.TapeInfo ], title: String, sizeVariant: String = "small" )
extends OverlayPanel // extends JFrame( title )
with Model {
   panel =>

   import TapesPanel._

   private object ColumnEnum {
      private var allVar = Vector.empty[ Column ]
      lazy val COLUMNS = allVar.toArray

      private def column( name: String, minWidth: Int, maxWidth: Int, extract: TapeInfo => Any,
                          renderer: Option[ TableCellRenderer ] = None,
                          sorter: Option[ Comparator[ _ ]] = None ) : Column = {
         val c = Column( allVar.size, name, minWidth, maxWidth, extract( _ ), renderer, sorter )
         allVar :+= c
         c
      }

//      private def columnR( name: String, minWidth: Int, maxWidth: Int, extract: TapeInfo => Any,
//                          renderer: Option[ TableCellRenderer ] = None,
//                          sorter: Option[ Comparator[ _ ]] = None ) : Column = {
//         val c = Column( allVar.size, name, minWidth, maxWidth, extract, renderer, sorter )
//         allVar :+= c
//         c
//      }

//      val COL_ID     = column( "ID",   56, 56, _.id, Some( RightAlignedRenderer ), Some( LongComparator ))
//      val COL_DOWN   = if( downloadPath.isDefined ) {
//         columnR( "Download", 64, 64, _.download, Some( DownloadRenderer ), Some( DownloadComparator ))
//      } else UnknownColumn
      
      val COL_NAME   = column( "Name", 96, 384, _.file.getName )
      val COL_FORM   = column( "Form", 36, 36, _.spec.fileType )
      val COL_CHAN   = column( "Ch", 24, 24, _.spec.numChannels, Some( RightAlignedRenderer ), Some( IntComparator ))
      val COL_BITS   = column( "Bit", 24, 24, _.spec.sampleFormat.bitsPerSample, Some( RightAlignedRenderer ), Some( IntComparator ))
      val COL_SR     = column( "kHz",  48, 48, _.spec.sampleRate / 1000, Some( RightAlignedRenderer ), Some( DoubleComparator ))
      val COL_DUR    = column( "Duration", 46, 46, info => info.spec.numFrames/ info.spec.sampleRate, Some( DurationRenderer ), Some( DoubleComparator ))
//      val COL_DESCR  = column( "Description", 96, 384, _.descriptions.headOption.map( _.text ).getOrElse( "" ))
//      val COL_USER   = column( "User", 56, 84, _.user.name )
      val COL_DATE   = column( "Date", 78, 78, info => new java.util.Date( info.file.lastModified ), Some( DateRenderer ), Some( DateComparator ))
//      val COL_RATING = column( "\u2605", 29, 29, _.statistics.rating, Some( RatingRenderer ), Some( IntComparator ))
   }
   val NUM_COLUMNS   = ColumnEnum.COLUMNS.size
   import ColumnEnum._

   private val ggTable                       = new JTable( SampleTableModel )
//   private val mapToRowIndices: Map[ TapeInfo, Int ] = infos.zipWithIndex[ TapeInfo, Map[ TapeInfo, Int ]]( breakOut );
//   private var tableModel : SampleTableModel = _
//   private val reprs: Array[ SampleRepr ] = samples.map( SampleRepr( _, None ))( breakOut )

   // ---- constructor ----
   {
//      val panel = getContentPane
      ggTable.putClientProperty( "JComponent.sizeVariant", sizeVariant )
      val rowSorter     = new TableRowSorter( SampleTableModel )
      val colModel      = ggTable.getColumnModel
      COLUMNS foreach { case col =>
         col.sorter.foreach( rowSorter.setComparator( col.idx, _ ))
         val tc = colModel.getColumn( col.idx )
         tc.setMinWidth( col.minWidth )
         tc.setMaxWidth( col.maxWidth )
         col.renderer.foreach( tc.setCellRenderer( _ ))
      }
      colModel.setColumnMargin( 6 )
      ggTable.setRowSorter( rowSorter )
      ggTable.addMouseListener( new MouseAdapter {
         override def mouseClicked( e: MouseEvent ) {
            if( e.getClickCount == 2 ) getParent.remove( panel )
         }
      })
      ggTable.getSelectionModel.addListSelectionListener( new ListSelectionListener {
         def valueChanged( e: ListSelectionEvent ) {
            val smps = ggTable.getSelectedRows.map( idx =>
               infos( ggTable.convertRowIndexToModel( idx )))
            dispatch( SelectionChanged( smps: _* ))
         }
      })
      ggTable.setBackground( Color.black )
      ggTable.setForeground( Color.white )
      val ggScroll = new JScrollPane( ggTable,
         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS )
      panel.setLayout( new BorderLayout() )
      panel.add( ggScroll, BorderLayout.CENTER )
//      queryFrame.addListener( frameListener )
//      search.addListener( searchListener )
//      pack()

//      setSize( 640, 480 )
      setPreferredSize( new Dimension( 640, 480 ))
      setSize( getPreferredSize )
      validate()
   }

//   private def defer( thunk: => Unit ) {
//      EventQueue.invokeLater( new Runnable { def run = thunk })
//   }

   private object SampleTableModel extends AbstractTableModel {
      def getRowCount = infos.size
      def getColumnCount = NUM_COLUMNS
      override def getColumnName( colIdx: Int ) = COLUMNS( colIdx ).name

      def getValueAt( rowIdx: Int, colIdx: Int ) : AnyRef = {
         val info = infos( rowIdx )
//         if( colIdx == COL_ID.idx ) return smp.id.asInstanceOf[ AnyRef ]
//         if( smp.info.isDefined ) {
            COLUMNS( colIdx ).extract( info ).asInstanceOf[ AnyRef ]
//         } else {
//            addInfoQuery( repr )
//            null
//         }
      }
   }
}