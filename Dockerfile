FROM python:3
WORKDIR /opt

RUN pip install flask requests wasp-eureka
COPY Density_map_data.py /opt/
COPY Controller_density_Map.py /opt/
COPY BI_density_mapsV1.json /opt/

EXPOSE 5000

CMD [ "python", "./Controller_density_Map.py" ]
