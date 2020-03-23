update purchase set attachment_id = null where attachment_id in (select id from attachment where file_path is null);

update shipment set attachment_id = null where attachment_id in (select id from attachment where file_path is null);

delete from attachment where file_path is null;

update shipment set number = '20190290-1' where id in (select id from shipment where number = '20190290' limit 1);
update shipment set number = '20190299-1' where id in (select id from shipment where number = '20190299' limit 1);
update shipment set number = '20190285-1' where id in (select id from shipment where number = '20190285' limit 1);
update shipment set number = '20190302-1' where id in (select id from shipment where number = '20190302' limit 1);
update shipment set number = '20190317-1' where id in (select id from shipment where number = '20190317' limit 1);
update shipment set number = '20190268-1' where id in (select id from shipment where number = '20190268' limit 1);
update shipment set number = '20190264-1' where id in (select id from shipment where number = '20190264' limit 1);
update shipment set number = '20190264-2' where id in (select id from shipment where number = '20190264' limit 1);
update shipment set number = '20190264-3' where id in (select id from shipment where number = '20190264' limit 1);
update shipment set number = '20200206001-1' where id in (select id from shipment where number = '20200206001' limit 1);
update shipment set number = '20190286-1' where id in (select id from shipment where number = '20190286' limit 1);
update shipment set number = '20190314-1' where id in (select id from shipment where number = '20190314' limit 1);
update shipment set number = '20190314-2' where id in (select id from shipment where number = '20190314' limit 1);
update shipment set number = '20190330-1' where id in (select id from shipment where number = '20190330' limit 1);
update shipment set number = '20190282-1' where id in (select id from shipment where number = '20190282' limit 1);
update shipment set number = 'MN01916159-1' where id in (select id from shipment where number = 'MN01916159' limit 1);
update shipment set number = '20190267-1' where id in (select id from shipment where number = '20190267' limit 1);
update shipment set number = '20190301-1' where id in (select id from shipment where number = '20190301' limit 1);
update shipment set number = '20190321-1' where id in (select id from shipment where number = '20190321' limit 1);
update shipment set number = '20190321-2' where id in (select id from shipment where number = '20190321' limit 1);
update shipment set number = '20190281-1' where id in (select id from shipment where number = '20190281' limit 1);
update shipment set number = '20190251-1' where id in (select id from shipment where number = '20190251' limit 1);
update shipment set number = '20190300-1' where id in (select id from shipment where number = '20190300' limit 1);
update shipment set number = '20190284-1' where id in (select id from shipment where number = '20190284' limit 1);
update shipment set number = '20190316-1' where id in (select id from shipment where number = '20190316' limit 1);
update shipment set number = '20190320-1' where id in (select id from shipment where number = '20190320' limit 1);
update shipment set number = '20190320-2' where id in (select id from shipment where number = '20190320' limit 1);
update shipment set number = '20190326-1' where id in (select id from shipment where number = '20190326' limit 1);
update shipment set number = '20190266-1' where id in (select id from shipment where number = '20190266' limit 1);
update shipment set number = 'SO2286247-10' where id in (select id from shipment where number = 'SO2286247-9' limit 1);
update shipment set number = '20190308-1' where id in (select id from shipment where number = '20190308' limit 1);
update shipment set number = '20190287-1' where id in (select id from shipment where number = '20190287' limit 1);
update shipment set number = '20190287-2' where id in (select id from shipment where number = '20190287' limit 1);
update shipment set number = '20190340-1' where id in (select id from shipment where number = '20190340' limit 1);
update shipment set number = '20190298-1' where id in (select id from shipment where number = '20190298' limit 1);

update item set number = 'C285-1' where id in (select id from item where number = 'C285' limit 1);
update item set number = 'C271-1' where id in (select id from item where number = 'C271' limit 1);
update item set number = 'C410-1' where id in (select id from item where number = 'C410' limit 1);
update item set number = 'C568-1' where id in (select id from item where number = 'C568' limit 1);
update item set number = 'C43-1' where id in (select id from item where number = 'C43' limit 1);
update item set number = 'C03-1' where id in (select id from item where number = 'C03' limit 1);
update item set number = 'C368-1' where id in (select id from item where number = 'C368' limit 1);
update item set number = 'C368-2' where id in (select id from item where number = 'C368' limit 1);
update item set number = 'C543-1' where id in (select id from item where number = 'C543' limit 1);
update item set number = 'C230-1' where id in (select id from item where number = 'C230' limit 1);
update item set number = 'G02-1' where id in (select id from item where number = 'G02' limit 1);
update item set number = 'C335-1' where id in (select id from item where number = 'C335' limit 1);
update item set number = 'C335-2' where id in (select id from item where number = 'C335' limit 1);

update component set number = '56-1' where id in (select id from component where number = '56' limit 1);
update component set number = '45-1' where id in (select id from component where number = '45' limit 1);
update component set number = '45-2' where id in (select id from component where number = '45' limit 1);
update component set number = '456-1' where id in (select id from component where number = '456' limit 1);
update component set number = '456-2' where id in (select id from component where number = '456' limit 1);
update component set number = '456-3' where id in (select id from component where number = '456' limit 1);
update component set number = '00-1' where id in (select id from component where number = '00' limit 1);
update component set number = '65-1' where id in (select id from component where number = '65' limit 1);
update component set number = '9-1' where id in (select id from component where number = '9' limit 1);
update component set number = '55-1' where id in (select id from component where number = '55' limit 1);
update component set number = '8-1' where id in (select id from component where number = '8' limit 1);
update component set number = '8-2' where id in (select id from component where number = '8' limit 1);
update component set number = '4-1' where id in (select id from component where number = '4' limit 1);
update component set number = '21-1' where id in (select id from component where number = '21' limit 1);
update component set number = '1234-1' where id in (select id from component where number = '1234' limit 1);


