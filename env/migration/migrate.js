const pg = require('pg');
const pool = new pg.Pool({
	//host: 'mpb.noovitec.com',
	host: 'localhost',
	port: '5432',
	database: 'mpb',
	user: 'postgres',
	password: 's3cret',
});

var query = "";
for(i = 101; i < 5000; i++){
//	query += "INSERT INTO public.component (id, case_pack, container_cost, delivery_cost, depth, description, duty_percentage, height, name, number, other_cost, supplier_stock_number, total_landed_cost, unit_cost, units_in_transit, units_on_stack, units_ordered, units_per_container, units_received, units_reserved, weight, width, attachment_id, category_id, supplier_id) VALUES ("+i+", 1, 3.00, 3.00, 3, NULL, 3.00, 10, '005', '100"+i+"', 1.00, NULL, 6.00, 2.00, 10, 30, 0, 1, 0, NULL, 4.00, 6, NULL, 2, 1); ";
query += "INSERT INTO public.item (id, case_depth, case_height, case_pack, case_weight, case_width, depth, description, height, hi, labor_cost, name, number, other_cost, package_cost, status, ti, total_cost, units_on_stack, units_scheduled, warehouse_cost, weight, width, attachment_id, brand_id, case_upc_id, category_id, season_id, upc_id) VALUES ("+i+", 0, 0, 1, 0.00, 0, 0, NULL, 0, 1, 0.00, '66', 'C6', 0.00, 12.00, 'DYNAMIC', 1, 32.20, NULL, NULL, 12.00, 0.00, 0, NULL, 2, 520, 5, 1, 520); ";

}

pool.query(query, (err, res) => {
	console.log(res, err);
	pool.end();
});
