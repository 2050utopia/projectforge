/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.plugins.eed.wicket;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.projectforge.business.fibu.EmployeeDO;
import org.projectforge.business.fibu.EmployeeTimedDO;
import org.projectforge.business.fibu.api.EmployeeService;
import org.projectforge.framework.time.DateHelper;
import org.projectforge.web.common.timeattr.AttrModel;
import org.projectforge.web.wicket.CellItemListener;
import org.projectforge.web.wicket.flowlayout.InputPanel;

import de.micromata.genome.db.jpa.tabattr.api.TimeableService;

/**
 * Supports CellItemListener.
 * 
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
@SuppressWarnings("serial")
public class AttrInputCellItemListenerPropertyColumn<T> extends PropertyColumn<T, String>
{
  protected CellItemListener<T> cellItemListener;

  private TimeableService<Integer, EmployeeTimedDO> timeableService;

  private EmployeeService employeeService;

  private String groupAttribute;

  /**
   * @param displayModelString For creation of new Model<String>.
   * @param sortProperty
   * @param propertyExpression
   * @param cellItemListener
   */
  public AttrInputCellItemListenerPropertyColumn(final IModel<String> displayModel, final String sortProperty,
      final String propertyExpression, final String groupAttribute,
      final CellItemListener<T> cellItemListener, TimeableService<Integer, EmployeeTimedDO> timeableService,
      EmployeeService employeeService)
  {
    super(displayModel, sortProperty, propertyExpression);
    this.cellItemListener = cellItemListener;
    this.groupAttribute = groupAttribute;
    this.timeableService = timeableService;
    this.employeeService = employeeService;
  }

  /**
   * Override this method if you want to have tool-tips.
   * 
   * @return
   */
  public String getTooltip(final T object)
  {
    return null;
  }

  /**
   * Call CellItemListener. If a property model object is of type I18nEnum then the translation is automatically used.
   * 
   * @see org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn#populateItem(org.apache.wicket.markup.repeater.Item,
   *      java.lang.String, org.apache.wicket.model.IModel)
   * @see CellItemListener#populateItem(Item, String, IModel)
   */
  @Override
  public void populateItem(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> rowModel)
  {
    final EmployeeDO employee = (EmployeeDO) rowModel.getObject();
    List<EmployeeTimedDO> timedAttributes = timeableService.getTimeableAttrRowsForGroupName(employee,
        getPropertyExpression());
    EmployeeTimedDO row = getAttrRowForSameMonth(timedAttributes, new Date());
    if (row == null) {
      row = employeeService.addNewTimeAttributeRow(employee, getPropertyExpression());
    }
    AttrModel<BigDecimal> attrModel = new AttrModel<>(row, groupAttribute, BigDecimal.class);
    item.add(new InputPanel(componentId, new TextField<BigDecimal>(InputPanel.WICKET_ID, attrModel)));
    if (cellItemListener != null) {
      cellItemListener.populateItem(item, componentId, rowModel);
    }
  }

  private EmployeeTimedDO getAttrRowForSameMonth(final List<EmployeeTimedDO> attrRows,
      Date dateToSelectAttrRow)
  {
    return attrRows
        .stream()
        .filter(row -> (row.getStartTime() != null && DateHelper.isSameMonth(row.getStartTime(), dateToSelectAttrRow)))
        .findFirst()
        .orElse(null);
  }
}