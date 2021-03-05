select distinct c.id as id, c.number as number, c.name as name, cat.name as categoryName, ct.name as componentTypeName, 
	su.name as supplierName, su.id as supplierId, 
	coalesce(sold.units_sold,0)-coalesce(sold.units_adjusted,0) as units_sold_adj,
	coalesce(purchase.units_purchased,0) as units_purchased,
	coalesce(rec.units_received,0) as units_received, 
	coalesce(prod.units_produced,0) as units_produced, 
	coalesce(shipped.units_shipped,0) as units_shipped,
	coalesce(sold.units_assigned,0) as units_assigned,
	coalesce(adjusted.units_adjusted,0) as units_adjusted, 
	((coalesce(rec.units_received,0)+coalesce(adjusted.units_adjusted,0))-coalesce(sold.units_assigned,0)) as units_on_stock, 
	((coalesce(rec.units_received,0)+coalesce(adjusted.units_adjusted,0))-coalesce(prod.units_produced,0)) as units_on_floor, 
	((coalesce(sold.units_sold,0)+coalesce(sold.units_adjusted,0))-coalesce(sold.units_assigned,0)) as units_pend_assignment, 
	(coalesce(purchase.units_purchased,0)-coalesce(rec.units_received,0)) as units_pend_receiving,
	((((coalesce(rec.units_received,0)+coalesce(adjusted.units_adjusted,0))-coalesce(sold.units_assigned,0)) 
		+  (coalesce(purchase.units_purchased,0)-coalesce(rec.units_received,0)))
		-  ((coalesce(sold.units_sold,0)+coalesce(sold.units_adjusted,0))-coalesce(sold.units_assigned,0))) as units_extra	
	from Component c 
left join (select distinct ic.component_id as cid, ceil(sum(si.units*ic.units)) as units_sold, ceil(sum(si.units_adjusted*ic.units)) as units_adjusted, 
		ceil(sum(si.units_assigned*ic.units)) as units_assigned
	from item_component ic join item i on i.id = ic.item_id 
	join item_packaging ip on ip.item_id = i.id 
	join sale_item si on si.item_packaging_id = ip.id 
	group by ic.component_id) sold on sold.cid = c.id 
left join (select distinct pc.component_id as cid, sum(pc.units) as units_purchased 
	from purchase_component pc 
	group by pc.component_id) purchase on purchase.cid = c.id 
left join (select distinct pc.component_id as cid, sum(r.units) as units_received 
	from purchase_component pc 
	join receiving r on r.purchase_component_id = pc.id 
	group by pc.component_id) rec on rec.cid = c.id 
left join (select distinct ic.component_id as cid, ceil(sum(p.units_produced*ic.units)) as units_produced 
	from item_component ic 
	join item i on i.id = ic.item_id join item_packaging ip on ip.item_id = i.id 
	join schedule_event se on se.item_packaging_id = ip.id 
	join production p on p.schedule_event_id = se.id 
	group by ic.component_id) prod on prod.cid = c.id 
left join (select ca.component_id as cid, sum(ca.units_adjusted) as units_adjusted 
	from component_adjustment ca 
	group by ca.component_id) adjusted on adjusted.cid = c.id 
left join (select distinct ic.component_id as cid, ceil(sum(ship_item.units*ic.units)) as units_shipped 
	from item_component ic 
	join item i on i.id = ic.item_id 
	join item_packaging ip on ip.item_id = i.id 
	join sale_item si on si.item_packaging_id = ip.id 
	join shipment_item ship_item on ship_item.sale_item_id = si.id 
	group by ic.component_id) shipped on shipped.cid = c.id 
left join item_component ic on ic.component_id = c.id 
left join item i on i.id = ic.item_id 
left join supplier su on su.id = c.supplier_id 
left join category cat on cat.id = c.category_id 
left join component_type ct on ct.id = c.component_type_id 
where c.id is not null 